package com.example.ecommerce.services;

import com.example.ecommerce.exceptions.ServiceUnavailable;
import com.example.ecommerce.models.EnergyMonitoringData;
import com.example.ecommerce.repositories.EnergyMonitoringRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
//            LocalDate today = LocalDate.now(ZoneOffset.UTC);

//            Instant startOfDay = today.atStartOfDay(ZoneOffset.UTC).toInstant();


//            Instant currentTime = Instant.now();

            String startStr = "2026-03-29 00:00:00+00:00";
            String currentStr = "2026-03-29 23:05:00+00:00";

            // 1. Define the pattern matching your exact string layout
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssXXX");

            // 2. Parse into a ZonedDateTime first (handles the offset safely)
            ZonedDateTime zonedDateTimeStart = ZonedDateTime.parse(startStr, formatter);
            ZonedDateTime zonedDateTimeCurrent = ZonedDateTime.parse(currentStr, formatter);

            // 3. Convert directly to an Instant
            Instant startOfDay = zonedDateTimeStart.toInstant();
            Instant currentTime = zonedDateTimeCurrent.toInstant();

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

    @Override
    public double getTotalProductionForResident() {
        return 0;
    }

    @Override
    public double getTotalConsumptionForResident() {
        return 0;
    }


}
