# Energy Saver App — Backend

Spring Boot backend that loads a year of energy-monitoring data (per site) into an H2 database and exposes calculations like today's generation/consumption, energy and money saved, and the next best time to use energy or charge a car.

## Prerequisites

- **Java 17+** and Maven (the project uses the Maven wrapper `mvnw`, so a separate Maven install isn't required)
- **Python 3** (only to generate the mock CSV data — no Python packages needed, standard library only)

## First-time setup

The app needs two large CSV files that are **not** stored in Git (they're ~200–250 MB each). You generate them locally from the committed script. This only has to be done once.

### 1. Generate the mock data

The generator script lives in `src/main/resources/`. From the project root:

```bash
cd src/main/resources
python generate_mock_data.py
cd ../../..
```

This writes two files next to the script:

- `src/main/resources/resident_mock.csv`
- `src/main/resources/carport_mock.csv`

It takes a couple of minutes per file. The output is reproducible (the random seed is fixed), so everyone gets identical files.

> If `python` isn't found, try `python3`. The script needs no dependencies.

### 2. (If needed) Remove a stale database file

The app stores its H2 database on disk at `data/testdb.mv.db`. On a **clean checkout this won't exist yet** — skip this step. You only need it if:

- You changed the entity/schema and are getting DDL or column errors on startup, **or**
- You get a database file-lock error (`90028`, "unable to obtain isolated JDBC connection"), **or**
- The data looks wrong/partial and you want a clean reload.

To force a clean rebuild, stop the app and delete the database file:

```bash
# from the project root
# Windows (PowerShell):
del data\testdb.mv.db
# macOS / Linux:
rm data/testdb.mv.db
```

On the next start, the app recreates the database and reloads the CSVs from scratch.

> The database rebuilds itself from the CSVs, so deleting it is always safe — you never lose anything that isn't regenerated.

### 3. Run the app

From the project root:

```bash
# Windows:
mvnw.cmd spring-boot:run
# macOS / Linux:
./mvnw spring-boot:run
```

**The first startup is slow.** It parses several million CSV rows and inserts them into H2 — expect a few minutes. Watch the console for `Loading resident_mock.csv ...` / `Finished ...` messages.

**Every startup after that is fast.** The loader checks whether the database already has data and skips reloading if so. You'll see:

```
Database already populated. Skipping CSV data load.
```

That message on the second run means everything worked.

## Verifying it worked

Once running, open the H2 console at:

```
http://localhost:8080/h2-console
```

Connect with:

- **JDBC URL:** `jdbc:h2:file:./data/testdb`
- **User Name:** `sa`
- **Password:** *(leave blank)*

> The JDBC URL must match exactly. If you connect to `jdbc:h2:mem:testdb` instead, you'll open a different, empty database and see no data.

A quick sanity check:

```sql
SELECT COUNT(*) FROM energy_monitoring_data;
SELECT site, COUNT(*) FROM energy_monitoring_data GROUP BY site;
```

You should see millions of rows, split across the sites (`resident`, `carport`).

## Common problems

**`create table ... [*]value ...` DDL error on startup**
A reserved-word column clash. If it recurs after a schema change, delete `data/testdb.mv.db` and restart to rebuild cleanly.

**`90028` / "unable to obtain isolated JDBC connection"**
Something already has the database file open — usually a previous run that didn't shut down cleanly, or a standalone H2 console. Stop all running instances of the app, close any external H2 console, then restart. If it persists, delete `data/testdb.mv.db`.

**`Invalid character found in method name [0x16 0x03 0x01 ...]`**
A client sent an HTTPS request to the plain-HTTP server. Use `http://localhost:8080`, not `https://`. This is a client URL issue, not a server bug — the app keeps running.

**Endpoints return `0.0` for "today"**
The mock data is for the year **2026**. Make sure the CSVs actually loaded (check the row count above) and that any date-based query is comparing in **UTC** (the timestamps are stored as UTC).

**Queries are slow**
Confirm the composite index on `(site, field, commodity_category, time)` exists — without it, aggregate queries scan the whole table. Check in the H2 console under the table's indexes, or via `INFORMATION_SCHEMA`.

## Notes

- **Data is synthetic.** The mock CSVs are physically-plausible (seasonal solar, daily load curves) but generated, not measured. They're for development and exercising the calculations, not real readings.
- **Don't commit the CSVs or the database.** `.gitignore` excludes `*_mock.csv`, `data/`, and `*.mv.db`. Only the generator script is committed. If you ever see a huge file staged for commit, stop — it shouldn't be tracked.
- **Sites** are distinguished by a `site` column on each row, set by the loader per file (`resident`, `carport`). Queries filter by site to keep the data separate.