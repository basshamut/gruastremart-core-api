package com.gruastremart.api.integration.cucumber.steps;

import com.gruastremart.api.controller.CraneDemandController;
import com.gruastremart.api.dto.CraneDemandResponseDto;
import com.gruastremart.api.exception.ServiceException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootTest
@RequiredArgsConstructor
public class CraneDemandSteps {

    private final CraneDemandController craneDemandController;

    private CraneDemandResponseDto craneDemandResponseDto;
    private ResponseEntity<Void> voidResponseEntity;
    private Exception thrownException;

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

    // Step definitions para cancelación de solicitudes
    @Given("no existe una solicitud con el ID {string}")
    public void noExistAdvisorById(String userId) {
        log.info("Ejecutado el GIVEN - solicitud no existe");
        // Este step asume que el ID proporcionado no existe en la base de datos
    }

    @When("el usuario cancela la solicitud con ID {string}")
    public void theUserCancelsDemand(String userId) {
        log.info("Ejecutado el WHEN - cancelar solicitud");
        voidResponseEntity = craneDemandController.cancelCraneDemand(userId);
    }

    @When("el usuario intenta cancelar la solicitud con ID {string}")
    public void theUserTriesToCancelDemand(String userId) {
        log.info("Ejecutado el WHEN - intentar cancelar solicitud inexistente");
        try {
            craneDemandController.cancelCraneDemand(userId);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("la solicitud se cancela exitosamente")
    public void theDemandIsCancelledSuccessfully() {
        log.info("Ejecutado el THEN - solicitud cancelada exitosamente");
        assertThat("La respuesta debe ser NO_CONTENT", voidResponseEntity.getStatusCode(), is(HttpStatus.NO_CONTENT));
    }

    // Step definitions para completar solicitudes
    @When("el operador completa la solicitud con ID {string}")
    public void theOperatorCompletesDemand(String userId) {
        log.info("Ejecutado el WHEN - completar solicitud");
        voidResponseEntity = craneDemandController.completeCraneDemand(userId);
    }

    @When("el operador intenta completar la solicitud con ID {string}")
    public void theOperatorTriesToCompleteDemand(String userId) {
        log.info("Ejecutado el WHEN - intentar completar solicitud inexistente");
        try {
            craneDemandController.completeCraneDemand(userId);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("la solicitud se completa exitosamente")
    public void theDemandIsCompletedSuccessfully() {
        log.info("Ejecutado el THEN - solicitud completada exitosamente");
        assertThat("La respuesta debe ser NO_CONTENT", voidResponseEntity.getStatusCode(), is(HttpStatus.NO_CONTENT));
    }

    @Then("se muestra un error de solicitud no encontrada")
    public void anErrorIsShownForNotFoundDemand() {
        log.info("Ejecutado el THEN - error de solicitud no encontrada");
        assertThat("Debe haberse lanzado una excepción", thrownException, is(notNullValue()));
        assertThat("La excepción debe ser ServiceException", thrownException instanceof ServiceException, is(true));
        ServiceException serviceException = (ServiceException) thrownException;
        assertThat("El mensaje debe indicar que no se encontró la solicitud", serviceException.getMessage(), is("Crane demand not found"));
    }
}
