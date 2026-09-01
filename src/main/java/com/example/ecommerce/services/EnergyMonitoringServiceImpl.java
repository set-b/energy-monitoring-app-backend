package com.example.ecommerce.services;

import com.example.ecommerce.exceptions.ServiceUnavailable;
import com.example.ecommerce.models.Customer;
import com.example.ecommerce.repositories.EnergyMonitoringRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

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
    public String getAllEnergyData() {
        try {

        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new ServiceUnavailable(); // change error later to relevant TODO
        }
//        try {
//            if (customer.isEmpty()) {
//                List<Customer> customers = customerRepository.findAll();
//                customers.sort(Comparator.comparing(Customer::getId));
//                return customers;
//            } else {
//                Example<Customer> customerExample = Example.of(customer);
//                return customerRepository.findAll(customerExample);
//            }
//        } catch (Exception e) {
//            logger.error(e.getMessage());
//            throw new ServiceUnavailable(e);
//        }
        return "";
    }
}
