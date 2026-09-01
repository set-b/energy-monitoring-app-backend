package com.example.ecommerce.controllers;

import com.example.ecommerce.services.EnergyMonitoringService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

import static com.example.ecommerce.constants.StringConstants.*;

@Tag(name = "Energy Monitoring Controller", description = "Energy management API")
@RestController
@RequestMapping(CONTEXT_ENERGY)
public class EnergyMonitoringController {

    private final Logger logger = LoggerFactory.getLogger(EnergyMonitoringController.class);
    private final EnergyMonitoringService energyMonitoringService;

    public EnergyMonitoringController(EnergyMonitoringService energyMonitoringService) {
        this.energyMonitoringService = energyMonitoringService;
    }

    // TODO returns all CSV data
    @GetMapping
    @Operation(summary = "Query Energy Data",
            description = "Get all energy monitoring data",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Energy monitoring data csv")
            })
    // TODO create an entity representing the columns of the CSV file
    public ResponseEntity<String> getResidentCSVData() {
        logger.info(new Date() + GET_REQUEST_ENERGY + ENERGY_DATA);

        return new ResponseEntity<>(energyMonitoringService.getAllEnergyData(), HttpStatus.OK);
    }

}
