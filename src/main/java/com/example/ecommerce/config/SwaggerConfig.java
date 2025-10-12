package com.example.ecommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;

/**
 * This class configures the swagger documentation for the project, which can also be used for
 * manual testing of the endpoints in the controller
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Ecommerce REST API")
                        .version("1.1")
                        .description("Java REST API project")
                        .termsOfService("Sample")
                        .contact(new Contact()
                                .name("Brandyn Tse")
                                .email("brandyntse941@gmail.com")
                                .url("https://github.com/set-b"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public-api")
                .pathsToMatch("/**")
                .pathsToExclude("/error", "/error/**")
                .build();
    }
}
