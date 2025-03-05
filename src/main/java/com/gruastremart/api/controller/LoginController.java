package com.gruastremart.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gruastremart.api.dto.LoginRequestJson;
import com.gruastremart.api.dto.LoginResponseJson;
import com.gruastremart.api.service.JwtService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.gruastremart.api.utils.Constants.API_VERSION_PATH;

@RestController
@RequestMapping(value = API_VERSION_PATH)
@Validated
@RequiredArgsConstructor
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    @PostMapping(value = "/login", produces = "application/json", consumes = "application/json")
    public ResponseEntity<LoginResponseJson> login(@RequestBody @NotNull LoginRequestJson loginRequestJson) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestJson.getUsername(), loginRequestJson.getPassword()));
        if (authentication.isAuthenticated()) {
            var token = jwtService.generateToken(authentication);
            return ResponseEntity.ok().body(LoginResponseJson.builder().token(token).type("Bearer").build());
        } else {
            throw new UsernameNotFoundException("invalid user request");
        }
    }
}
