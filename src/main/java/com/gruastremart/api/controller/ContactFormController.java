package com.gruastremart.api.controller;

import com.gruastremart.api.dto.ContactFormCreateRequestDto;
import com.gruastremart.api.dto.ContactFormResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.gruastremart.api.utils.Constants.API_VERSION_PATH;

@RestController
@RequestMapping(API_VERSION_PATH + "/contact-forms")
public class ContactFormController {

    //private final ContactFormService contactFormService;

    @PostMapping
    public ResponseEntity<ContactFormResponseDto> createContactForm(@RequestBody ContactFormCreateRequestDto owner) {
        var created = ContactFormResponseDto.builder()
                .email("test@test.com")
                .name("Test")
                .messageResponse("Ok")
                .build();
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }


}
