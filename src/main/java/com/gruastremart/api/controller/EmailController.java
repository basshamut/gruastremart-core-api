package com.gruastremart.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gruastremart.api.dto.EmailRequestDto;
import com.gruastremart.api.service.EmailService;

@RestController
@RequestMapping("/v1/emails")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/contact")
    public ResponseEntity<?> sendContactEmail(@RequestBody EmailRequestDto emailRequest) {
        emailService.sendContactEmail(emailRequest);
        return ResponseEntity.ok().body("Correo enviado correctamente");
    }
}
