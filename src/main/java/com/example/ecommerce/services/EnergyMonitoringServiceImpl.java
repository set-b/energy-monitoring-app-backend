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

import static com.example.ecommerce.constants.StringConstants.*;

/**
 * This Class contains methods from the EnergyMonitoringService interface.
 * This is used to process and return CSV data
 */
@Service
public class EnergyMonitoringServiceImpl implements EnergyMonitoringService {

    private final Logger logger = LoggerFactory.getLogger(EnergyMonitoringService.class);

    @Autowired
    private EnergyMonitoringRepository energyMonitoringRepository;

    /**
     * This method returns all CSV data, unprocessed
     *
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

            return energyMonitoringRepository.findAllBySiteAndTimestampBetween(RESIDENT, startOfDay, currentTime);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new ServiceUnavailable();
        }
    }



    @Override
    public double getTotalConsumptionForToday() {
        Instant now = Instant.now();
        Instant startOfToday = now.atZone(ZoneOffset.UTC).toLocalDate()
                .atStartOfDay(ZoneOffset.UTC).toInstant();

        return energyMonitoringRepository.sumPower(RESIDENT,"POW", "consumption", startOfToday, now);
    }

    @Override
    public double getTotalProductionForToday() {
        Instant now = Instant.now();
        Instant startOfToday = now.atZone(ZoneOffset.UTC).toLocalDate()
                .atStartOfDay(ZoneOffset.UTC).toInstant();

        return Math.abs(energyMonitoringRepository.sumPower(RESIDENT,"POW", "generation", startOfToday, now));
    }

    private static final int EARLIEST_HOUR = 7;
    private static final int LATEST_HOUR = 22;

    @Override
    public int getNextBestTimeHours() {
        Instant now = Instant.now();
        Instant windowStart = now.minus(60, ChronoUnit.DAYS);

        List<Object[]> rows = energyMonitoringRepository.avgSupplyByHour(RESIDENT, windowStart, now);

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
    public int getNextBestTimeHoursCarport() {
        Instant now = Instant.now();
        Instant windowStart = now.minus(60, ChronoUnit.DAYS);

        List<Object[]> rows = energyMonitoringRepository.avgSupplyByHour(CARPORT, windowStart, now);

        Map<Integer, Double> avgByHour = new HashMap<>();
        for (Object[] row : rows) {
            int hour = ((Number) row[0]).intValue();
            double avgSupply = ((Number) row[1]).doubleValue();
            avgByHour.put(hour, avgSupply);
        }

        int currentHour = now.atZone(ZoneOffset.UTC).getHour();

        int bestOffset = -1;
        double bestSupply = Double.POSITIVE_INFINITY;   // most negative = most surplus

        for (int offset = 0; offset < 24; offset++) {
            int hour = (currentHour + offset) % 24;
            if (hour < EARLIEST_HOUR || hour > LATEST_HOUR) continue;

            Double avg = avgByHour.get(hour);
            if (avg == null) continue;

            if (avg < bestSupply) {
                bestSupply = avg;
                bestOffset = offset;
            }
        }

        return bestOffset;
    }



    /**
     * Calculates the total value for the requested data source and commodity categories.
     * <p>
     * The dataSource parameter is currently a placeholder that documents whether the
     * calculation belongs to resident or community-house data. It must be added to
     * the repository query when a data-source column is available in the entity.
     *
     * @param dataSource          data source used by the calculation
     * @param commodityCategories categories included in the calculation
     * @return sum of the matching energy values
     */
    private double getTotalByDataSourceAndCommodityCategories(
            String dataSource,
            List<String> commodityCategories
    ) {
        Instant currentTime = Instant.now();

        logger.debug(
                "Calculating energy total for data source {} and categories {}",
                dataSource,
                commodityCategories
        );

        return energyMonitoringRepository
                .sumValueBySiteAndTimestampBeforeAndFieldAndCommodityCategoryIn(
                        dataSource,
                        currentTime,
                        "POW",
                        commodityCategories
                );
    }



    /**
     * Returns the resident's total electricity consumption.
     *
     * @return total consumption as a positive value
     * NOTEBOOK INFORMATION ->
     * consumption today X
     * - get all rows with same instant with today's date and time equal to or less than the current time
     * - if CTYPEC == consumption then add up the _value fields  (test)
     * <p>
     * create mock data for this
     */
    @Override
    public double getTotalConsumptionForResident() {
        return getTotalByDataSourceAndCommodityCategories(
                RESIDENT,
                List.of("consumption")
        );
    }



    /**
     *
     * Returns the resident's total electricity generation.
     *
     * @return total generation as stored in the dataset, usually negative
     * NOTEBOOK INFORMATION ->
     * - get all rows with same instant with today's date and time equal to or less than the current time
     * - conditional to call helper function
     * - if CTYPEC == generation then add up the _value fields
     */

    public double getTotalGenerationForResident() {
        return Math.abs(getTotalByDataSourceAndCommodityCategories(
                RESIDENT,
                List.of("generation"))
        );
    }

    /**
     * Calculates the neighborhood's net energy balance.
     * <p>
     * Generation values are stored as negative numbers and consumption values as
     * positive numbers. The signed total is negated so that a positive result
     * represents a surplus and a negative result represents a deficit.
     *
     * @return positive for surplus, negative for deficit, or zero when balanced
     * NOTEBOOK INFORMATION ->
     * -- from last reading surplus/deficit in energy production - neighborhood (this from notebook)
     */
    public double getDeficitAndSurplusOfTheNeighborhood() {
        double signedNetEnergy =
                getTotalByDataSourceAndCommodityCategories(
                        COMMUNITY_HOUSING,
                        List.of("consumption", "generation")
                );

        return -1 * signedNetEnergy;



    }

}
