package com.gruastremart.api.controller;

import com.gruastremart.api.controller.handler.json.HttpErrorInfoJson;
import com.gruastremart.api.dto.OperatorLocationDto;
import com.gruastremart.api.dto.OperatorLocationRequestDto;
import com.gruastremart.api.service.OperatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static com.gruastremart.api.utils.constants.Constants.API_VERSION_PATH;

@Slf4j
@RestController
@RequestMapping(value = API_VERSION_PATH + "/operators")
@RequiredArgsConstructor
@Tag(name = "Operator Controller", description = "API para manejar datos de operadores")
public class OperatorController {

    private final OperatorService operatorService;

    @Operation(summary = "Actualizar localización del operador",
            description = "Guarda o actualiza las coordenadas de localización de un operador en cache")
    @ApiResponse(responseCode = "200", description = "Localización actualizada correctamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = OperatorLocationDto.class)))
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = HttpErrorInfoJson.class)))
    @ApiResponse(responseCode = "401", description = "No autorizado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = HttpErrorInfoJson.class)))
    @PutMapping("/{operatorId}/location")
    public ResponseEntity<OperatorLocationDto> updateOperatorLocation(
            @PathVariable String operatorId,
            @Valid @RequestBody OperatorLocationRequestDto request,
            HttpServletRequest httpRequest) {

        OperatorLocationDto location = operatorService.saveOperatorLocation(operatorId, request);
        return new ResponseEntity<>(location, HttpStatus.OK);
    }

    @Operation(summary = "Obtener localización del operador",
            description = "Obtiene las coordenadas de localización de un operador desde cache")
    @ApiResponse(responseCode = "200", description = "Localización encontrada",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = OperatorLocationDto.class)))
    @ApiResponse(responseCode = "404", description = "Localización no encontrada")
    @ApiResponse(responseCode = "401", description = "No autorizado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = HttpErrorInfoJson.class)))
    @GetMapping("/{operatorId}/location")
    public ResponseEntity<OperatorLocationDto> getOperatorLocation(@PathVariable String operatorId) {

        Optional<OperatorLocationDto> location = operatorService.getOperatorLocation(operatorId);

        return location.map(loc -> new ResponseEntity<>(loc, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Verificar si operador tiene localización",
            description = "Verifica si un operador tiene localización guardada en cache")
    @ApiResponse(responseCode = "200", description = "Estado verificado")
    @GetMapping("/{operatorId}/location/status")
    public ResponseEntity<Boolean> checkOperatorLocationStatus(@PathVariable String operatorId) {

        boolean hasLocation = operatorService.isOperatorLocationCached(operatorId);

        return new ResponseEntity<>(hasLocation, HttpStatus.OK);
    }
}
