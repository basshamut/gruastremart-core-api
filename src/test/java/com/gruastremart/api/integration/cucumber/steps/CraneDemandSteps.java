package com.gruastremart.api.integration.cucumber.steps;

import com.gruastremart.api.controller.CraneDemandController;
import com.gruastremart.api.dto.CraneDemandResponseDto;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

@Slf4j
@SpringBootTest
@RequiredArgsConstructor
public class CraneDemandSteps {

    private final CraneDemandController craneDemandController;

    private CraneDemandResponseDto craneDemandResponseDto;

    @Given("existe una solicitud con el ID {string}")
    public void existAdvisorById(String userId) {
        log.info("Ejecutado el GIVEN");

        var responseEntity = craneDemandController.findById(userId);
        var resultBody = responseEntity.getBody();

        // Validamos que el propietario existe
        assertThat("La solicitud debería existir en la base de datos", resultBody, is(notNullValue()));
    }

    @When("el usuario consulta los detalles de la solicitud con ID {string}")
    public void theAdvisorDetailsByID(String userId) {
        log.info("Ejecutado el WHEN");

        var responseEntity = craneDemandController.findById(userId);
        craneDemandResponseDto = responseEntity.getBody();

        // Validamos que se obtuvo una respuesta
        assertThat("La respuesta no debe ser nula", craneDemandResponseDto, is(notNullValue()));
    }

    @Then("se muestran los detalles de la solicitud")
    public void theAdvisorDetailsAreDisplayed() {
        log.info("Ejecutado el THEN");

        // Validamos que el ID no sea nulo
        assertThat("El ID de la solicitud no debe ser nulo", craneDemandResponseDto.getId(), is(notNullValue()));

        // Validamos que haya información básica
        assertThat("La descripcion de la solicitud no debe estar vacía", craneDemandResponseDto.getDescription(), is(not(emptyOrNullString())));
    }
}
