package com.example.ecommerce.repositories;

import com.example.ecommerce.models.Customer;
import com.example.ecommerce.models.EnergyMonitoringData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * This repository is used for saving CSV file as a String. **This will be changed in the future**
 * Long is the {id} of the CSV file being loaded
 */
@Repository
public interface EnergyMonitoringRepository extends JpaRepository<EnergyMonitoringData, Long> {
}
