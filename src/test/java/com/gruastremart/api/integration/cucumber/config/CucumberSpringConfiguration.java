package com.gruastremart.api.integration.cucumber.config;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@CucumberContextConfiguration
@SpringBootTest
@TestPropertySource(properties = {
        "spring.data.mongodb.uri=${MONGODB_TEST_URL}"
})
public class CucumberSpringConfiguration {
}