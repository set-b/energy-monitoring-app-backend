package com.example.ecommerce.services;

import com.example.ecommerce.exceptions.ServiceUnavailable;
import com.example.ecommerce.models.EnergyMonitoringData;
import com.example.ecommerce.repositories.EnergyMonitoringRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This Class contains methods from the EnergyMonitoringService interface.
 * This is used to process and return CSV data
 */
@Service
public class EnergyMonitoringServiceImpl implements EnergyMonitoringService{

    private final Logger logger = LoggerFactory.getLogger(EnergyMonitoringService.class);

    @Autowired
    private EnergyMonitoringRepository energyMonitoringRepository;

    /**
     * This method returns all CSV data, unprocessed
     * @return String
     */
    @Override
    public List<EnergyMonitoringData> getAllEnergyData() {
        try {
            return energyMonitoringRepository.findAll();
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new ServiceUnavailable(); // change error later to relevant TODO
        }
    }

    @Override
    public List<EnergyMonitoringData> getEnergyDataForToday() {
        try {
            LocalDate today = LocalDate.now(ZoneOffset.UTC);

            Instant startOfDay = today.atStartOfDay(ZoneOffset.UTC).toInstant();


            Instant currentTime = Instant.now();

             return energyMonitoringRepository.findAllByTimestampBetween(startOfDay, currentTime);
        } catch (Exception e){
            logger.error(e.getMessage());
            throw new ServiceUnavailable();
        }
    }



    // TODO create string context for consumption or production
    @Override
    public double getTotalConsumptionForToday() {
        Instant now = Instant.now();
        Instant startOfToday = now.atZone(ZoneOffset.UTC).toLocalDate()
                .atStartOfDay(ZoneOffset.UTC).toInstant();

        return energyMonitoringRepository.sumPower("POW", "consumption", startOfToday, now);
    }

    @Override
    public double getTotalProductionForToday() {
        Instant now = Instant.now();
        Instant startOfToday = now.atZone(ZoneOffset.UTC).toLocalDate()
                .atStartOfDay(ZoneOffset.UTC).toInstant();

        return energyMonitoringRepository.sumPower("POW", "generation", startOfToday, now);
    }

    private static final int EARLIEST_HOUR = 7;
    private static final int LATEST_HOUR = 22;

    @Override
    public int getNextBestTimeHours() {
        Instant now = Instant.now();
        Instant windowStart = now.minus(60, ChronoUnit.DAYS);

        List<Object[]> rows = energyMonitoringRepository.avgSupplyByHour(windowStart, now);

        Map<Integer, Double> avgByHour = new HashMap<>();
        for (Object[] row : rows) {
            int hour = ((Number) row[0]).intValue();
            double avgSupply = ((Number) row[1]).doubleValue();
            avgByHour.put(hour, avgSupply);
        }

        int currentHour = now.atZone(ZoneOffset.UTC).getHour();

        int bestOffset = -1;
        double bestSupply = Double.POSITIVE_INFINITY;   // most negative = most surplus

        // scan the next 24 hours starting now, wrapping past midnight
        for (int offset = 0; offset < 24; offset++) {
            int hour = (currentHour + offset) % 24;
            if (hour < EARLIEST_HOUR || hour > LATEST_HOUR) continue;  // skip overnight

            Double avg = avgByHour.get(hour);
            if (avg == null) continue;

            if (avg < bestSupply) {
                bestSupply = avg;
                bestOffset = offset;   // hours from now
            }
        }

        return bestOffset;   // 0..47 realistically; -1 only if no data at all
    }

    @Override
    public double getTotalProductionForResident() {
        return 0;
    }

    @Override
    public double getTotalConsumptionForResident() {
    return 0;
    }
}
