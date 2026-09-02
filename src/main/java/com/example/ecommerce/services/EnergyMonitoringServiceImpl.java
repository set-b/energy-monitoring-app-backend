package com.example.ecommerce.services;

import com.example.ecommerce.exceptions.ServiceUnavailable;
import com.example.ecommerce.models.EnergyMonitoringData;
import com.example.ecommerce.repositories.EnergyMonitoringRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
}
