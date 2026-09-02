package com.example.ecommerce.controllers;

import com.example.ecommerce.models.EnergyMonitoringData;
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
import java.util.List;

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

    @GetMapping
    @Operation(summary = "Query Energy Data",
            description = "Get all energy monitoring data",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Energy monitoring data csv")
            })
    public ResponseEntity<List<EnergyMonitoringData>> getResidentCSVData() {
        logger.info(new Date() + GET_REQUEST_ENERGY + ENERGY_DATA);

        return new ResponseEntity<>(energyMonitoringService.getAllEnergyData(), HttpStatus.OK);
    }

    @GetMapping("/today")
    @Operation(summary = "Get Energy Data for today",
            description = "Get all energy monitoring data for today",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Energy monitoring data csv")
            })
    public ResponseEntity<List<EnergyMonitoringData>> getResidentCSVDataForToday() {
        logger.info(new Date() + GET_REQUEST_ENERGY + ENERGY_DATA);

        return new ResponseEntity<>(energyMonitoringService.getEnergyDataForToday(), HttpStatus.OK);
    }

    @GetMapping("/today/consumption")
    @Operation(summary = "Get Total Energy Consumption Data for today",
            description = "Get all energy monitoring data for today's consumption",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Energy monitoring data csv")
            })
    public ResponseEntity<Double> getResidentTotalConsumptionForToday() {
        logger.info(new Date() + GET_REQUEST_ENERGY + ENERGY_DATA);

        return new ResponseEntity<>(energyMonitoringService.getTotalConsumptionForToday(), HttpStatus.OK);
    }

    @GetMapping("/today/production")
    @Operation(summary = "Get Total Energy Production Data for today",
            description = "Get all energy monitoring data for today's production",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Energy monitoring data csv")
            })
    public ResponseEntity<Double> getResidentTotalProductionForToday() {
        logger.info(new Date() + GET_REQUEST_ENERGY + ENERGY_DATA);

        return new ResponseEntity<>(energyMonitoringService.getTotalProductionForToday(), HttpStatus.OK);
    }

    @GetMapping("/today/next-best-time")
    @Operation(summary = "Get Total Energy Production Data for today",
            description = "Get all energy monitoring data for today's production",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Energy monitoring data csv")
            })
    public ResponseEntity<Integer> getNextBestTimeToUseEnergy() {
        logger.info(new Date() + GET_REQUEST_ENERGY + ENERGY_DATA);

        return new ResponseEntity<>(energyMonitoringService.getNextBestTimeHours(), HttpStatus.OK);
    }






    @GetMapping("/resident/consumption/overall")
    @Operation(summary = "Get Total Energy Consumption Data for resident",
            description = "Get all energy monitoring data for resident's consumption",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Total Energy consumption data")
            })
    public ResponseEntity<Double> getResidentTotalConsumption() {
        logger.info(new Date() + GET_REQUEST_ENERGY + ENERGY_DATA);

        return new ResponseEntity<>(energyMonitoringService.getTotalConsumptionForResident(), HttpStatus.OK);
    }

    @GetMapping("/resident/production/overall")
    @Operation(summary = "Get Total Energy Production Data for resident",
            description = "Get all energy monitoring data for resident's production",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Total Energy production data ")
            })
    public ResponseEntity<Double> getTotalGenerationForResident() {
        logger.info(new Date() + GET_REQUEST_ENERGY + ENERGY_DATA);

        return new ResponseEntity<>(energyMonitoringService.getTotalGenerationForResident(), HttpStatus.OK);
    }



    @GetMapping("/neighborhood/subtract/overall")
    @Operation(summary = "Subtract production and consumption Data for neighborhood",
            description = "Subtract data for the neighborhood ",
            responses = {
                    @ApiResponse(responseCode = "200", description = "from last reading surplus/deficit in energy production - neighborhood")
            })
    public ResponseEntity<Double> getDeficitAndSurplusOfTheNeighborhood() {
        logger.info(new Date() + GET_REQUEST_ENERGY + ENERGY_DATA);

        return new ResponseEntity<>(energyMonitoringService.getDeficitAndSurplusOfTheNeighborhood(), HttpStatus.OK);
    }

}
