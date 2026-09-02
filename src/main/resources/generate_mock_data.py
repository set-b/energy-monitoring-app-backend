#!/usr/bin/env python3
"""
generate_mock_data.py

Generates a full year (2026) of mock energy-monitoring data for two sites:
  - resident_mock.csv   (household: solar, boiler, dishwasher, washing machine, powermeter)
  - carport_mock.csv    (carport:  large solar, EV charger, battery, powermeter)

Both match the schema of the real data:
  columns: _time,_value,CTYPE,CTYPEC,ETYPE,_field
  - 5-minute cadence, all 7 field types (POW, POW_APP, POW_FACT, CURR, ENE_CNT_IMP, ENE_CNT_EXP, PRICE)
  - POW in watts; solar generation is NEGATIVE, consumption POSITIVE
  - ENE_CNT_* counters accumulate monotonically (so last-first = kWh)
  - realistic seasonal solar (strong summer, weak winter) + daily sun curve + weather noise

Usage:
    python generate_mock_data.py

Output: writes both CSVs into the current directory. Move them into
src/main/resources/ for the Spring Boot CsvLoader to pick them up.

Note: RNG is seeded, so output is reproducible (everyone gets identical files).
Each file is ~200-250 MB and takes a couple of minutes to write.
"""

import csv
import math
import random
from datetime import datetime, timedelta, timezone

# ---------------------------------------------------------------- config
YEAR = 2026
STEP_MIN = 5
PRICE = 0.254084

START = datetime(YEAR, 1, 1, tzinfo=timezone.utc)
END = datetime(YEAR + 1, 1, 1, tzinfo=timezone.utc)
STEP_HOURS = STEP_MIN / 60.0


# ---------------------------------------------------------------- helpers
def fmt_time(dt):
    return dt.strftime("%Y-%m-%d %H:%M:%S+00:00")


def solar_season(doy):
    """Seasonal scaling: ~1.0 at summer solstice (~day 172), ~0.35 in winter."""
    phase = math.cos(2 * math.pi * (doy - 172) / 365.0)
    return 0.35 + 0.65 * (phase + 1) / 2.0


def daylight_hours(doy):
    """Rough NL daylight: ~8h winter, ~16h summer."""
    return 12 + 4 * math.cos(2 * math.pi * (doy - 172) / 365.0) * -1


def solar_power(dt, peak_watts):
    """Generation power in watts (NEGATIVE). 0 at night. Bell curve across daylight."""
    doy = dt.timetuple().tm_yday
    hour = dt.hour + dt.minute / 60.0
    dl = daylight_hours(doy)
    sunrise = 13 - dl / 2
    sunset = 13 + dl / 2
    if hour < sunrise or hour > sunset:
        return 0.0
    x = (hour - 13) / (dl / 2)
    shape = max(0.0, math.cos(x * math.pi / 2))
    weather = random.uniform(0.5, 1.0)
    return -(peak_watts * solar_season(doy) * shape * weather)


def derive_fields(pow_w):
    """Plausible apparent power (VA), power factor, current (A) from active power."""
    pf = random.uniform(0.92, 0.99) if abs(pow_w) > 1 else random.uniform(0.5, 0.95)
    app = abs(pow_w) / pf if pf > 0 else 0.0
    curr = app / 230.0
    return app, pf, curr


class Counters:
    """Monotonic import/export energy counters (kWh) keyed by name."""
    def __init__(self):
        self.data = {}

    def accumulate(self, key, pow_w):
        ik, ek = key + ":IMP", key + ":EXP"
        self.data.setdefault(ik, 0.0)
        self.data.setdefault(ek, 0.0)
        energy = abs(pow_w) * STEP_HOURS / 1000.0
        if pow_w >= 0:
            self.data[ik] += energy
        else:
            self.data[ek] += energy
        return self.data[ik], self.data[ek]


def base_consumption(dt):
    """Household baseline load in watts (positive): morning + evening peaks."""
    hour = dt.hour + dt.minute / 60.0
    morning = 900 * math.exp(-((hour - 8) ** 2) / 3.0)
    evening = 1400 * math.exp(-((hour - 20) ** 2) / 6.0)
    base = 180
    return (base + morning + evening) * random.uniform(0.85, 1.15)


def appliance_power(dt, cycle_hours, peak_w, prob_start, state):
    """Intermittent appliance. Returns watts (positive). Runs occasional cycles."""
    if state["remaining"] > 0:
        state["remaining"] -= 1
        return peak_w * random.uniform(0.4, 1.0)
    daytime = 1.5 if 8 <= dt.hour <= 22 else 0.2
    if random.random() < prob_start * daytime:
        state["remaining"] = int(cycle_hours * 60 / STEP_MIN)
        return peak_w * random.uniform(0.4, 1.0)
    return 0.0


def evcharger_power(dt, state):
    """EV charging sessions: up to ~11 kW for a few hours, mostly daytime."""
    if state["remaining"] > 0:
        state["remaining"] -= 1
        return state["rate"] * random.uniform(0.9, 1.0)
    daytime = 2.0 if 8 <= dt.hour <= 18 else 0.3
    if random.random() < 0.01 * daytime:
        state["remaining"] = int(random.uniform(1.5, 4.0) * 60 / STEP_MIN)
        state["rate"] = random.uniform(7000, 11000)
        return state["rate"] * random.uniform(0.9, 1.0)
    return 0.0


def battery_power(solar, ev):
    """Battery storage POW (positive), charges from surplus solar."""
    surplus = -solar - ev
    if surplus > 500:
        return min(surplus * random.uniform(0.2, 0.5), 30000) + random.uniform(50, 150)
    return random.uniform(55, 200)


# ---------------------------------------------------------------- generators
def generate_resident(path):
    random.seed(42)
    counters = Counters()
    dishwasher = {"remaining": 0}
    washer = {"remaining": 0}
    boiler = {"remaining": 0}
    rows = 0

    with open(path, "w", newline="") as f:
        w = csv.writer(f, lineterminator="\n")
        w.writerow(["_time", "_value", "CTYPE", "CTYPEC", "ETYPE", "_field"])
        dt = START
        while dt < END:
            t = fmt_time(dt)

            # solar (generation, negative) ~3kW peak
            sp = solar_power(dt, 3000)
            app, pf, cu = derive_fields(sp)
            im, ex = counters.accumulate("solar", sp)
            w.writerow([t, sp, "electricity", "generation", "solarpanel", "POW"])
            w.writerow([t, round(app, 4), "electricity", "generation", "solarpanel", "POW_APP"])
            w.writerow([t, round(pf, 4), "electricity", "generation", "solarpanel", "POW_FACT"])
            w.writerow([t, round(cu, 4), "electricity", "generation", "solarpanel", "CURR"])
            w.writerow([t, round(im, 4), "electricity", "generation", "solarpanel", "ENE_CNT_IMP"])
            w.writerow([t, round(ex, 4), "electricity", "generation", "solarpanel", "ENE_CNT_EXP"])

            # appliances (consumption, blank CTYPEC)
            bp = appliance_power(dt, 0.5, 1000, 0.04, boiler)
            dp = appliance_power(dt, 1.5, 1900, 0.015, dishwasher)
            wp = appliance_power(dt, 1.5, 1800, 0.015, washer)
            for dev, val in [("boiler", bp), ("dishwasher", dp), ("washingmachine", wp)]:
                app, pf, cu = derive_fields(val)
                im, ex = counters.accumulate(dev, val)
                w.writerow([t, round(val, 4), "electricity", "", dev, "POW"])
                w.writerow([t, round(app, 4), "electricity", "", dev, "POW_APP"])
                w.writerow([t, round(pf, 4), "electricity", "", dev, "POW_FACT"])
                w.writerow([t, round(cu, 4), "electricity", "", dev, "CURR"])
                w.writerow([t, round(im, 4), "electricity", "", dev, "ENE_CNT_IMP"])
                w.writerow([t, round(ex, 4), "electricity", "", dev, "ENE_CNT_EXP"])

            # powermeter
            total_cons = base_consumption(dt) + bp + dp + wp
            net = total_cons + sp
            imc, exc = counters.accumulate("pm_cons", total_cons)
            w.writerow([t, round(total_cons, 4), "electricity", "consumption", "powermeter", "POW"])
            w.writerow([t, round(imc, 4), "electricity", "consumption", "powermeter", "ENE_CNT_IMP"])
            w.writerow([t, round(exc, 4), "electricity", "consumption", "powermeter", "ENE_CNT_EXP"])
            imn, exn = counters.accumulate("pm_net", net)
            w.writerow([t, round(net, 4), "electricity", "", "powermeter", "POW"])
            w.writerow([t, round(imn, 4), "electricity", "", "powermeter", "ENE_CNT_IMP"])
            w.writerow([t, round(exn, 4), "electricity", "", "powermeter", "ENE_CNT_EXP"])
            app, pf, cu = derive_fields(net)
            ims, exs = counters.accumulate("pm_sup", net)
            w.writerow([t, round(net, 4), "electricity", "supply", "powermeter", "POW"])
            w.writerow([t, round(cu, 4), "electricity", "supply", "powermeter", "CURR"])
            w.writerow([t, round(ims, 4), "electricity", "supply", "powermeter", "ENE_CNT_IMP"])
            w.writerow([t, round(exs, 4), "electricity", "supply", "powermeter", "ENE_CNT_EXP"])

            # price
            w.writerow([t, PRICE, "electricity", "consumption", "priceprovider", "PRICE"])

            rows += 1
            dt += timedelta(minutes=STEP_MIN)
    return rows


def generate_carport(path):
    random.seed(43)
    counters = Counters()
    ev_state = {"remaining": 0, "rate": 0.0}
    rows = 0

    with open(path, "w", newline="") as f:
        w = csv.writer(f, lineterminator="\n")
        w.writerow(["_time", "_value", "CTYPE", "CTYPEC", "ETYPE", "_field"])
        dt = START
        while dt < END:
            t = fmt_time(dt)

            # solar (generation, negative) ~50kW peak
            sp = solar_power(dt, 50000)
            app, pf, cu = derive_fields(sp)
            im, ex = counters.accumulate("solar", sp)
            w.writerow([t, sp, "electricity", "generation", "solarpanel", "POW"])
            w.writerow([t, round(app, 4), "electricity", "generation", "solarpanel", "POW_APP"])
            w.writerow([t, round(pf, 4), "electricity", "generation", "solarpanel", "POW_FACT"])
            w.writerow([t, round(cu, 4), "electricity", "generation", "solarpanel", "CURR"])
            w.writerow([t, round(im, 4), "electricity", "generation", "solarpanel", "ENE_CNT_IMP"])
            w.writerow([t, round(ex, 4), "electricity", "generation", "solarpanel", "ENE_CNT_EXP"])

            # ev charger (consumption, blank CTYPEC)
            ev = evcharger_power(dt, ev_state)
            app, pf, cu = derive_fields(ev)
            im, ex = counters.accumulate("ev", ev)
            w.writerow([t, round(ev, 4), "electricity", "", "evcharger", "POW"])
            w.writerow([t, round(app, 4), "electricity", "", "evcharger", "POW_APP"])
            w.writerow([t, round(pf, 4), "electricity", "", "evcharger", "POW_FACT"])
            w.writerow([t, round(cu, 4), "electricity", "", "evcharger", "CURR"])
            w.writerow([t, round(im, 4), "electricity", "", "evcharger", "ENE_CNT_IMP"])
            w.writerow([t, round(ex, 4), "electricity", "", "evcharger", "ENE_CNT_EXP"])

            # battery (storage)
            bp = battery_power(sp, ev)
            im, ex = counters.accumulate("battery", bp)
            w.writerow([t, round(bp, 4), "electricity", "storage", "battery", "POW"])
            w.writerow([t, round(im, 4), "electricity", "storage", "battery", "ENE_CNT_IMP"])
            w.writerow([t, round(ex, 4), "electricity", "storage", "battery", "ENE_CNT_EXP"])

            # powermeter
            total_cons = ev + bp
            net = total_cons + sp
            imc, exc = counters.accumulate("pm_cons", total_cons)
            w.writerow([t, round(total_cons, 4), "electricity", "consumption", "powermeter", "POW"])
            w.writerow([t, round(imc, 4), "electricity", "consumption", "powermeter", "ENE_CNT_IMP"])
            w.writerow([t, round(exc, 4), "electricity", "consumption", "powermeter", "ENE_CNT_EXP"])
            imn, exn = counters.accumulate("pm_net", net)
            w.writerow([t, round(net, 4), "electricity", "", "powermeter", "POW"])
            w.writerow([t, round(imn, 4), "electricity", "", "powermeter", "ENE_CNT_IMP"])
            w.writerow([t, round(exn, 4), "electricity", "", "powermeter", "ENE_CNT_EXP"])
            app, pf, cu = derive_fields(net)
            ims, exs = counters.accumulate("pm_sup", net)
            w.writerow([t, round(net, 4), "electricity", "supply", "powermeter", "POW"])
            w.writerow([t, round(app, 4), "electricity", "supply", "powermeter", "POW_APP"])
            w.writerow([t, round(pf, 4), "electricity", "supply", "powermeter", "POW_FACT"])
            w.writerow([t, round(cu, 4), "electricity", "supply", "powermeter", "CURR"])
            w.writerow([t, round(ims, 4), "electricity", "supply", "powermeter", "ENE_CNT_IMP"])
            w.writerow([t, round(exs, 4), "electricity", "supply", "powermeter", "ENE_CNT_EXP"])

            # price
            w.writerow([t, PRICE, "electricity", "consumption", "priceprovider", "PRICE"])

            rows += 1
            dt += timedelta(minutes=STEP_MIN)
    return rows


# ---------------------------------------------------------------- main
if __name__ == "__main__":
    print("Generating resident_mock.csv (this takes a minute or two)...")
    r = generate_resident("resident_mock.csv")
    print(f"  done: {r} timestamps written")

    print("Generating carport_mock.csv (this takes a minute or two)...")
    c = generate_carport("carport_mock.csv")
    print(f"  done: {c} timestamps written")

    print("\nBoth files generated in the current directory.")
    print("Move them into src/main/resources/ for the Spring Boot CsvLoader.")
