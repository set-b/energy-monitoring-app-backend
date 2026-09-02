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

import static com.example.ecommerce.constants.StringConstants.CARPORT;
import static com.example.ecommerce.constants.StringConstants.RESIDENT;

@Component
public class CsvLoader {

    private final EnergyMonitoringRepository energyMonitoringRepository;

    public CsvLoader(EnergyMonitoringRepository energyMonitoringRepository) {
        this.energyMonitoringRepository = energyMonitoringRepository;
    }

    @PostConstruct
    public void load() throws IOException {
        if (energyMonitoringRepository.count() > 0) {
            System.out.println("Database already populated. Skipping.");
            return;
        }

        loadFile("resident_mock.csv", RESIDENT);
        loadFile("carport_mock.csv", CARPORT);
        // loadFile("community_house_mock.csv", "community_house");  // later
    }

    private void loadFile(String fileName, String site) throws IOException {
        ClassPathResource resource = new ClassPathResource(fileName);
        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            List<EnergyMonitoringData> data = new CsvToBeanBuilder<EnergyMonitoringData>(reader)
                    .withType(EnergyMonitoringData.class)
                    .build()
                    .parse();

            for (EnergyMonitoringData row : data) {
                row.setSite(site);   // tag every row with its origin
            }
            energyMonitoringRepository.saveAll(data);
        }
    }
}