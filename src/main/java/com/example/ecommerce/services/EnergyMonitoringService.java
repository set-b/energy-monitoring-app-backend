package com.example.ecommerce.services;

import com.example.ecommerce.models.EnergyMonitoringData;

import java.util.List;

// TODO change from String to entity and perhaps entity properties
public interface EnergyMonitoringService {

    /**
     * This function takes CSV data and returns all of it as a String
     * @return String
     */
    List<EnergyMonitoringData> getAllEnergyData();

    List<EnergyMonitoringData> getEnergyDataForToday();

    double getTotalConsumptionForToday();
    double getTotalProductionForToday();

    double getTotalGenerationForResident();
    int getNextBestTimeHours();

    int getNextBestTimeHoursCarport();

    double getTotalConsumptionForResident();

    double getDeficitAndSurplusOfTheNeighborhood();

    double getEnergySavedLastMonth(String site);

    double getMoneySavedLastMonth(String site);

}
