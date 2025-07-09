package com.gruastremart.api.controller;

import com.gruastremart.api.dto.HttpErrorInfoDto;
import com.gruastremart.api.dto.UserDto;
import com.gruastremart.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.gruastremart.api.utils.constants.Constants.API_VERSION_PATH;

@RestController
@RequestMapping(value = API_VERSION_PATH + "/users")
@Validated
@RequiredArgsConstructor
@Tag(name = "User Management", description = "API para gestión de usuarios")
public class UserController {

    private final UserService userService;

    @Operation(summary = "User Search", description = "Search users by filters")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class)))
    @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @GetMapping
    @Parameters({
            @Parameter(name = "page", description = "Número de página", required = true),
            @Parameter(name = "size", description = "Tamaño de la página", required = true),
            @Parameter(name = "email", description = "Filtrar por email"),
            @Parameter(name = "supabaseId", description = "Filtrar por supabaseId"),
            @Parameter(
                    name = "role",
                    description = "Filtrar por tipo de cuenta",
                    schema = @Schema(allowableValues = {"ADMIN", "CLIENT", "OPERATOR"})
            )
    })
    public ResponseEntity<Page<UserDto>> findWithFilters(@Parameter(description = "Query parameters for filtering users") @RequestParam(required = false) MultiValueMap<String, String> params) {
        var users = userService.findWithFilters(params);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @Operation(summary = "Register User", description = "Register a new user in the system")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class)))
    @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "409", description = "CONFLICT", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Parameter(description = "User registration data", required = true) @RequestBody UserDto userDto) {
        var response = userService.register(userDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Update User", description = "Update an existing user's information")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class)))
    @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoDto.class)))
    @PatchMapping("/{id}")
    public ResponseEntity<UserDto> update(@Parameter(description = "Unique identifier of the user to update", required = true) @PathVariable String id, @Parameter(description = "User data to update", required = true) @RequestBody UserDto userDto) {
        var response = userService.update(id, userDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
