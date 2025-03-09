package com.gruastremart.api.controller;

import static com.gruastremart.api.utils.Constants.API_VERSION_PATH;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @GetMapping(value = "/{userEmail}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable String userEmail) {
        var user = userService.findUserByEmail(userEmail);
        return ResponseEntity.ok().body(user);
    }
}
