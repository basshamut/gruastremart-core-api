package com.gruastremart.api.controller;

import com.gruastremart.api.dto.CranePricingResponseDto;
import com.gruastremart.api.dto.HttpErrorInfoDto;
import com.gruastremart.api.service.CranePricingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.gruastremart.api.utils.constants.Constants.API_VERSION_PATH;

@Slf4j
@RestController
@RequestMapping(value = API_VERSION_PATH + "/crane-pricing")
@RequiredArgsConstructor
@Tag(name = "Crane Pricing Management", description = "API para gestión de precios de grúas")
public class CranePricingController {

    private final CranePricingService cranePricingService;

    @Operation(summary = "Crane Pricing Search", description = "Search crane pricing by filters with pagination")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CranePricingResponseDto.class)))
    @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @GetMapping
    @Parameters({
            @Parameter(name = "page", description = "Número de página", required = true),
            @Parameter(name = "size", description = "Tamaño de la página", required = true),
            @Parameter(
                    name = "active",
                    description = "Filtrar por estado activo/inactivo",
                    schema = @Schema(allowableValues = {"true", "false"})
            ),
            @Parameter(name = "weight", description = "Peso del vehículo en kg para encontrar precios aplicables"),
            @Parameter(name = "weightCategory", description = "Nombre de la categoría de peso (búsqueda parcial)"),
            @Parameter(
                    name = "pricingType",
                    description = "Tipo de tarificación",
                    schema = @Schema(allowableValues = {"urbano", "extra_urbano"})
            ),
            @Parameter(name = "minUrbanPrice", description = "Precio urbano mínimo en USD"),
            @Parameter(name = "maxUrbanPrice", description = "Precio urbano máximo en USD"),
            @Parameter(name = "minExtraUrbanBasePrice", description = "Precio base extra-urbano mínimo en USD")
    })
    public ResponseEntity<Page<CranePricingResponseDto>> findWithFilters(
            @Parameter(description = "Query parameters for filtering crane pricing")
            @RequestParam(required = false) MultiValueMap<String, String> params) {
        log.info("Request to search crane pricing with filters: {}", params);
        Page<CranePricingResponseDto> pricing = cranePricingService.findWithFilters(params);
        return new ResponseEntity<>(pricing, HttpStatus.OK);
    }

    @Operation(summary = "Get Crane Pricing by ID", description = "Retrieve a specific crane pricing by its unique identifier")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CranePricingResponseDto.class)))
    @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @GetMapping("/{id}")
    public ResponseEntity<CranePricingResponseDto> findById(
            @Parameter(description = "Crane pricing unique identifier", required = true)
            @PathVariable String id) {
        log.info("Request to get crane pricing by id: {}", id);
        CranePricingResponseDto pricing = cranePricingService.findById(id);
        return new ResponseEntity<>(pricing, HttpStatus.OK);
    }
}

