package com.gruastremart.api.controller;

import static com.gruastremart.api.utils.constants.Constants.API_VERSION_PATH;

import com.gruastremart.api.controller.handler.json.HttpErrorInfoJson;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

import com.gruastremart.api.dto.UserDto;
import com.gruastremart.api.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = API_VERSION_PATH + "/users")
@Validated
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "User Search", description = "Search users by filters")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class)))
    @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoJson.class)))
    @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoJson.class)))
    @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoJson.class)))
    @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoJson.class)))
    @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpErrorInfoJson.class)))
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

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody UserDto userDto) {
        var response = userService.register(userDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("{id}")
    public ResponseEntity<UserDto> update(@PathVariable String id, @RequestBody UserDto userDto) {
        var response = userService.update(id, userDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
