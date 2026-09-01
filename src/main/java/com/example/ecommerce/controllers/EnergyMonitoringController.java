package com.example.ecommerce.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.ecommerce.constants.StringConstants.CONTEXT_ENERGY;

@Tag(name = "Customer Controller", description = "Customer management API")
@RestController
@RequestMapping(CONTEXT_ENERGY)
public class EnergyMonitoringController {

    private final Logger logger = LoggerFactory.getLogger(EnergyMonitoringController.class);



}
