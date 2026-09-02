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

//    List<EnergyMonitoringData> getCurrentEnergyDataForResident();

    double getTotalGenerationForResident();

    double getTotalConsumptionForResident();

    double getDeficitAndSurplusOfTheNeighborhood();
}
