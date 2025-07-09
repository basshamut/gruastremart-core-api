package com.gruastremart.api.controller;

import com.gruastremart.api.dto.HttpErrorInfoDto;
import com.gruastremart.api.dto.ContactFormCreateRequestDto;
import com.gruastremart.api.dto.ContactFormResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.gruastremart.api.utils.constants.Constants.API_VERSION_PATH;

@RestController
@RequestMapping(API_VERSION_PATH + "/contact-forms")
@Tag(name = "Contact Form Management", description = "API para gestión de formularios de contacto")
public class ContactFormController {

    //private final ContactFormService contactFormService;

    @Operation(summary = "Create Contact Form", description = "Create a new contact form submission")
    @ApiResponse(responseCode = "201", description = "CREATED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ContactFormResponseDto.class)))
    @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @PostMapping
    public ResponseEntity<ContactFormResponseDto> createContactForm(@Parameter(description = "Contact form data to submit", required = true) @RequestBody ContactFormCreateRequestDto contactFormCreateRequestDto ) {
        var created = ContactFormResponseDto.builder()
                .email("test@test.com")
                .name("Test")
                .messageResponse("Ok")
                .build();
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

}
