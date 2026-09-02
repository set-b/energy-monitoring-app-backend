package com.example.ecommerce.data;

import com.example.ecommerce.models.EnergyMonitoringData;
import com.example.ecommerce.repositories.EnergyMonitoringRepository;
import com.opencsv.bean.CsvToBeanBuilder;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class CsvLoader {

    private final EnergyMonitoringRepository energyMonitoringRepository;

    public CsvLoader(EnergyMonitoringRepository energyMonitoringRepository) {
        this.energyMonitoringRepository = energyMonitoringRepository;
    }

    @PostConstruct
    public void load() throws IOException {
        ClassPathResource resource = new ClassPathResource("resident.csv");
        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {

            List<EnergyMonitoringData> data = new CsvToBeanBuilder<EnergyMonitoringData>(reader)
                    .withType(EnergyMonitoringData.class)
                    .build()
                    .parse();

            energyMonitoringRepository.saveAll(data);
        }
    }
}