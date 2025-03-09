package com.gruastremart.api.controller;

import static com.gruastremart.api.utils.Constants.API_VERSION_PATH;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gruastremart.api.dto.LoginRequestDto;
import com.gruastremart.api.dto.LoginResponseDto;
import com.gruastremart.api.dto.UserDto;
import com.gruastremart.api.service.JwtService;
import com.gruastremart.api.service.UserService;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = API_VERSION_PATH + "/users")
@Validated
@RequiredArgsConstructor
public class UserController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    @PostMapping(value = "/login", produces = "application/json", consumes = "application/json")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @NotNull LoginRequestDto loginRequestDto) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword()));
        if (authentication.isAuthenticated()) {
            var token = jwtService.generateToken(authentication);
            return ResponseEntity.ok().body(LoginResponseDto.builder().token(token).type("Bearer").build());
        } else {
            throw new UsernameNotFoundException("invalid user request");
        }
    }

    @GetMapping(value = "/{userEmail}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable String userEmail) {
        var user = userService.findUserByEmail(userEmail);
        return ResponseEntity.ok().body(user);
    }
}
