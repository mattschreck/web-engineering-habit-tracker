package com.example.habittracker.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("Habit Tracker API")
                    .version("1.0.0")
                    .description("REST API for Habit Tracker application - A university project for Web Engineering course")
                    .contact(
                        Contact()
                            .name("Habit Tracker Team")
                            .email("support@habittracker.example.com")
                    )
            )
            .servers(
                listOf(
                    Server().url("http://localhost:8080").description("Local Development Server"),
                    Server().url("https://habittracker.onrender.com").description("Production Server")
                )
            )
    }
}
