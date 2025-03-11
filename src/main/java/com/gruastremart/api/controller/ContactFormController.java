package com.gruastremart.api.controller;

import com.gruastremart.api.dto.CraneDemandCreateRequestDto;
import com.gruastremart.api.dto.CraneDemandResponseDto;
import com.gruastremart.api.dto.CraneDemandUpdateRequestDto;
import com.gruastremart.api.exception.ServiceException;
import com.gruastremart.api.service.CraneDemandService;
import com.gruastremart.api.utils.Tools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/crane-requests")
public class ContactFormController {

    private final ContactFormService contactFormService;

    @PostMapping
    public ResponseEntity<ContactFormResponseDto> createContactForm(@RequestBody ContactFormCreateRequestDto owner) {
        var createdOwner = contactFormService.createContactform(owner);
        return new ResponseEntity<>(createdOwner, HttpStatus.CREATED);
    }
    

}
