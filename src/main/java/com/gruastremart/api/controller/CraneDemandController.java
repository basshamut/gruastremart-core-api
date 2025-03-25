package com.gruastremart.api.controller;

import com.gruastremart.api.controller.handler.json.HttpErrorInfoJson;
import com.gruastremart.api.dto.CraneDemandCreateRequestDto;
import com.gruastremart.api.dto.CraneDemandResponseDto;
import com.gruastremart.api.dto.CraneDemandUpdateRequestDto;
import com.gruastremart.api.dto.RequestMetadataDto;
import com.gruastremart.api.service.CraneDemandService;
import com.gruastremart.api.utils.tools.RequestMetadataExtractorUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.gruastremart.api.utils.constants.Constants.API_VERSION_PATH;

@RestController
@RequestMapping(value = API_VERSION_PATH + "/crane-demands")
@RequiredArgsConstructor
public class CraneDemandController {

    private final CraneDemandService craneDemandService;

    @Operation(summary = "Crane Demand Search", description = "Search crane demands by filters")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CraneDemandResponseDto.class)))
    @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoJson.class)))
    @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoJson.class)))
    @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoJson.class)))
    @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoJson.class)))
    @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoJson.class)))
    @GetMapping
    @Parameters({
            @Parameter(name = "page", description = "Número de página", required = true),
            @Parameter(name = "size", description = "Tamaño de la página", required = true),
    })
    public ResponseEntity<Page<CraneDemandResponseDto>> findWithFilters(@Parameter(description = "Query parameters for filtering crane demands") @RequestParam(required = false) MultiValueMap<String, String> params) {
        var users = craneDemandService.findWithFilters(params);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CraneDemandResponseDto> findById(@PathVariable String id) {
        try {
            var owner = craneDemandService.getCraneDemandById(id);
            return new ResponseEntity<>(owner, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<CraneDemandResponseDto> createCraneDemand(
            @RequestBody CraneDemandCreateRequestDto craneDemandRequest, HttpServletRequest request) {

        RequestMetadataDto meta = RequestMetadataExtractorUtil.extract(request);

        System.out.printf(
                "AUDITORÍA - Fecha: %s, Usuario: %s, Rol: %s, Email: %s, IP: %s, User-Agent: %s, Ubicación actual: %s, Destino: %s\n",
                meta.getTimestamp(), meta.getUserId(), meta.getRole(), meta.getEmail(),
                meta.getIp(), meta.getUserAgent(),
                craneDemandRequest.getCurrentLocation(), craneDemandRequest.getDestinationLocation());

        var createdOwner = craneDemandService.createCraneDemand(craneDemandRequest, meta.getEmail());
        return new ResponseEntity<>(createdOwner, HttpStatus.CREATED);
    }

    @PutMapping("/{craneDemandId}")
    public ResponseEntity<CraneDemandResponseDto> updateCraneDemand(@PathVariable String craneDemandId,
                                                                    @RequestBody CraneDemandUpdateRequestDto CraneDemand) {
        var updatedOwner = craneDemandService.updateCraneDemand(craneDemandId, CraneDemand);
        return updatedOwner.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCraneDemand(@PathVariable String id) {
        craneDemandService.deleteCraneDemand(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
