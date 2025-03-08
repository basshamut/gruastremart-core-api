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
public class CraneDemandController {

    private final CraneDemandService craneDemandService;

    @Autowired
    public CraneDemandController(CraneDemandService craneDemandService) {
        this.craneDemandService = craneDemandService;
    }

    @GetMapping
    public ResponseEntity<Page<CraneDemandResponseDto>> findWithFilters(@RequestParam int page, @RequestParam int size) {
        if (!Tools.isValidPagination(page, size)) {
            throw new ServiceException("Invalid pagination parameters", HttpStatus.BAD_REQUEST.value());
        }

        var pageable = Pageable.ofSize(size).withPage(page);
        var owners = craneDemandService.findWithFilters(pageable);
        return new ResponseEntity<>(owners, HttpStatus.OK);
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
    public ResponseEntity<CraneDemandResponseDto> createCraneDemand(@RequestBody CraneDemandCreateRequestDto owner) {
        var createdOwner = craneDemandService.createCraneDemand(owner);
        return new ResponseEntity<>(createdOwner, HttpStatus.CREATED);
    }

    @PutMapping("/{craneDemandId}")
    public ResponseEntity<CraneDemandResponseDto> updateCraneDemand(@PathVariable String craneDemandId, @RequestBody CraneDemandUpdateRequestDto CraneDemand) {
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
