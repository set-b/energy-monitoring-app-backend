package com.example.ecommerce.services;

import com.example.ecommerce.constants.EnergyDataSource;
import com.example.ecommerce.exceptions.ServiceUnavailable;
import com.example.ecommerce.models.EnergyMonitoringData;
import com.example.ecommerce.repositories.EnergyMonitoringRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new ServiceUnavailable();
        }
    }

    // TODO create string context for consumption or production
    @Override
    public double getTotalConsumptionForToday() {
        List<EnergyMonitoringData> todaysEnergyConsumptionData = getEnergyDataForToday();
        return todaysEnergyConsumptionData.stream()
                .filter(f -> f.getField().equals("POW") && f.getCommodityType().equals("consumption"))
                .mapToDouble(f -> f.getValue()) // Maps to a Primitive DoubleStream
                .sum();
    }

    @Override
    public double getTotalProductionForToday() {
        List<EnergyMonitoringData> todaysEnergyProductionData = getEnergyDataForToday();
        return todaysEnergyProductionData.stream()
                .filter(f -> f.getField().equals("POW") && f.getCommodityType().equals("production"))
                .mapToDouble(f -> f.getValue()) // Maps to a Primitive DoubleStream
                .sum();
    }



    /*--------------------------------------------------------------------------------------------------------*/

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
            EnergyDataSource dataSource,
            List<String> commodityCategories
    ) {
        Instant currentTime = Instant.now();

        logger.debug(
                "Calculating energy total for data source {} and categories {}",
                dataSource,
                commodityCategories
        );

        return energyMonitoringRepository
                .sumValueByTimestampBeforeAndFieldAndCommodityCategoryIn(
                        currentTime,
                        "POW",
                        commodityCategories
                );
    }


    //    //TODO this should be for RESIDENT - so we should have something to distinct it from community

    /**
     * Returns the resident's total electricity consumption.
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
                EnergyDataSource.RESIDENT,
                List.of("consumption")
        );
    }


    //    //TODO this should be for RESIDENT - so we should have something to distinct it from community

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
    @Override
    public double getTotalGenerationForResident() {
        return -getTotalByDataSourceAndCommodityCategories(
                EnergyDataSource.RESIDENT,
                List.of("generation")
        );
    }


//    //TODO this should be for CommunityHouse - so we should have something to distinct it from resident

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
    @Override
    public double getDeficitAndSurplusOfTheNeighborhood() {
        double signedNetEnergy =
                getTotalByDataSourceAndCommodityCategories(
                        EnergyDataSource.COMMUNITY_HOUSE,
                        List.of("consumption", "generation")
                );

        return -signedNetEnergy;
    }

    /*--------------------------------------------------------------------------------------------------------*/


}
