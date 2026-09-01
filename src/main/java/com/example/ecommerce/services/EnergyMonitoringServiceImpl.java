package com.example.ecommerce.services;

import com.example.ecommerce.repositories.CustomerRepository;
import com.example.ecommerce.repositories.EnergyMonitoringRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EnergyMonitoringServiceImpl {

    private final Logger logger = LoggerFactory.getLogger(EnergyMonitoringService.class);

    @Autowired
    private EnergyMonitoringRepository energyMonitoringRepository;
}
