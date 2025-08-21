package com.gruastremart.api.controller;

import com.gruastremart.api.dto.AssignCraneDemandDto;
import com.gruastremart.api.dto.CraneDemandCreateRequestDto;
import com.gruastremart.api.dto.CraneDemandResponseDto;
import com.gruastremart.api.dto.HttpErrorInfoDto;
import com.gruastremart.api.service.CraneDemandService;
import com.gruastremart.api.utils.tools.RequestMetadataExtractorUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.gruastremart.api.utils.constants.Constants.API_VERSION_PATH;

@Slf4j
@RestController
@RequestMapping(value = API_VERSION_PATH + "/crane-demands")
@RequiredArgsConstructor
@Tag(name = "Crane Demand Management", description = "API para gestión de demandas de grúas")
public class CraneDemandController {

    private final CraneDemandService craneDemandService;

    @Operation(summary = "Crane Demand Search", description = "Search crane demands by filters")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CraneDemandResponseDto.class)))
    @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @GetMapping
    @Parameters({
            @Parameter(name = "page", description = "Número de página", required = true),
            @Parameter(name = "size", description = "Tamaño de la página", required = true),
            @Parameter(name = "lat", description = "Latitud"),
            @Parameter(name = "lng", description = "Longitud"),
            @Parameter(name = "radio", description = "Radio de búsqueda"),
            @Parameter(
                    name = "state",
                    description = "Filtrar por estado de la demanda",
                    schema = @Schema(allowableValues = {"ACTIVE", "INACTIVE", "TAKEN", "COMPLETED", "CANCELLED"})
            ),
            @Parameter(name = "createdByUserId", description = "ID del usuario que creó la demanda"),
            @Parameter(
                    name = "startDate",
                    description = "Fecha de inicio para filtrar demandas (formato: yyyy-MM-dd o yyyy-MM-dd'T'HH:mm:ss)",
                    schema = @Schema(type = "string", format = "date-time")
            ),
            @Parameter(
                    name = "endDate",
                    description = "Fecha de fin para filtrar demandas (formato: yyyy-MM-dd o yyyy-MM-dd'T'HH:mm:ss)",
                    schema = @Schema(type = "string", format = "date-time")
            ),
            @Parameter(name = "createdByUserId", description = "ID del usuario que creó la demanda"),
            @Parameter(name = "assignedOperatorId", description = "ID del operador asignado a la demanda")
    })
    public ResponseEntity<Page<CraneDemandResponseDto>> findWithFilters(@Parameter(description = "Query parameters for filtering crane demands") @RequestParam(required = false) MultiValueMap<String, String> params) {
        var users = craneDemandService.findWithFilters(params);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @Operation(summary = "Get Crane Demand by ID", description = "Retrieve a specific crane demand by its unique identifier")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CraneDemandResponseDto.class)))
    @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @GetMapping("/{id}")
    public ResponseEntity<CraneDemandResponseDto> findById(@Parameter(description = "Unique identifier of the crane demand", required = true) @PathVariable String id) {
        var demand = craneDemandService.getCraneDemandById(id);
        return new ResponseEntity<>(demand, HttpStatus.OK);
    }

    @Operation(summary = "Create Crane Demand", description = "Create a new crane demand request")
    @ApiResponse(responseCode = "201", description = "CREATED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CraneDemandResponseDto.class)))
    @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @PostMapping
    public ResponseEntity<CraneDemandResponseDto> createCraneDemand(
            @Parameter(description = "Crane demand creation request data", required = true) @RequestBody CraneDemandCreateRequestDto craneDemandRequest, HttpServletRequest request) {

        var meta = RequestMetadataExtractorUtil.extract(request);
        var created = craneDemandService.createCraneDemand(craneDemandRequest, meta.getEmail());

        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @Operation(summary = "Assign Crane Demand", description = "Assign a crane demand to the current user with weight category")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CraneDemandResponseDto.class)))
    @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @PatchMapping("/{craneDemandId}/assign")
    public ResponseEntity<CraneDemandResponseDto> assignCraneDemand(
            @Parameter(description = "Unique identifier of the crane demand to assign", required = true)
            @PathVariable String craneDemandId,
            @RequestBody AssignCraneDemandDto assignCraneDemandDto) {
        var updated = craneDemandService.assignCraneDemand(craneDemandId, assignCraneDemandDto);

        return updated.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Cancel Crane Demand", description = "Cancel an existing crane demand")
    @ApiResponse(responseCode = "204", description = "NO CONTENT")
    @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelCraneDemand(@Parameter(description = "Unique identifier of the crane demand to cancel", required = true) @PathVariable String id) {
        craneDemandService.cancelCraneDemand(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

