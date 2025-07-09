package com.gruastremart.api.controller;

import com.gruastremart.api.dto.HttpErrorInfoDto;
import com.gruastremart.api.dto.OperatorLocationDto;
import com.gruastremart.api.dto.OperatorLocationRequestDto;
import com.gruastremart.api.service.OperatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@Tag(name = "Operator Management", description = "API para gestión de operadores")
public class OperatorController {

    private final OperatorService operatorService;

    @Operation(summary = "Update Operator Location", description = "Save or update the location coordinates of an operator in cache")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OperatorLocationDto.class)))
    @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @PutMapping("/{operatorId}/location")
    public ResponseEntity<OperatorLocationDto> updateOperatorLocation(
            @Parameter(description = "Unique identifier of the operator", required = true) @PathVariable String operatorId,
            @Parameter(description = "Location data to update", required = true) @Valid @RequestBody OperatorLocationRequestDto request,
            HttpServletRequest httpRequest) {

        OperatorLocationDto location = operatorService.saveOperatorLocation(operatorId, request);
        return new ResponseEntity<>(location, HttpStatus.OK);
    }

    @Operation(summary = "Get Operator Location", description = "Retrieve the location coordinates of an operator from cache")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OperatorLocationDto.class)))
    @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @GetMapping("/{operatorId}/location")
    public ResponseEntity<OperatorLocationDto> getOperatorLocation(@Parameter(description = "Unique identifier of the operator", required = true) @PathVariable String operatorId) {

        Optional<OperatorLocationDto> location = operatorService.getOperatorLocation(operatorId);

        return location.map(loc -> new ResponseEntity<>(loc, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Check Operator Location Status", description = "Verify if an operator has location data stored in cache")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class)))
    @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @GetMapping("/{operatorId}/location/status")
    public ResponseEntity<Boolean> checkOperatorLocationStatus(@Parameter(description = "Unique identifier of the operator", required = true) @PathVariable String operatorId) {

        boolean hasLocation = operatorService.isOperatorLocationCached(operatorId);

        return new ResponseEntity<>(hasLocation, HttpStatus.OK);
    }
}
