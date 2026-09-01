package com.example.ecommerce.data;

import com.example.ecommerce.models.EnergyMonitoringData;
import com.example.ecommerce.repositories.EnergyMonitoringRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * This class is used to load and parse CSV data to be saved in the EnergyMonitoringRepository
 * loads at runtime
 */
@Component
public class CsvLoader {
    @Autowired
    private final EnergyMonitoringRepository energyMonitoringRepository;

    public CsvLoader(EnergyMonitoringRepository energyMonitoringRepository) {
        this.energyMonitoringRepository = energyMonitoringRepository;
    }

    /**
     * This function reads CSV files and saves them to the repository as String.
     * This will be used to save mock data to the database as well.
     * @throws IOException
     */
    @PostConstruct
    public void load() throws IOException {
        ClassPathResource resource = new ClassPathResource("resident.csv");
        String content;
        try (InputStream is = resource.getInputStream()) {
            content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
        EnergyMonitoringData data = new EnergyMonitoringData();
        data.setContent(content);
        energyMonitoringRepository.save(data);
    }
}