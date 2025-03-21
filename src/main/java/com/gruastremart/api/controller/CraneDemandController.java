package com.gruastremart.api.controller;

import static com.gruastremart.api.utils.Constants.API_VERSION_PATH;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gruastremart.api.dto.CraneDemandCreateRequestDto;
import com.gruastremart.api.dto.CraneDemandResponseDto;
import com.gruastremart.api.dto.CraneDemandUpdateRequestDto;
import com.gruastremart.api.exception.ServiceException;
import com.gruastremart.api.service.CraneDemandService;
import com.gruastremart.api.utils.Tools;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = API_VERSION_PATH + "/crane-demands")
@RequiredArgsConstructor
public class CraneDemandController {

    private final CraneDemandService craneDemandService;

    @GetMapping
    public ResponseEntity<Page<CraneDemandResponseDto>> findWithFilters(@RequestParam int page,
            @RequestParam int size) {
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
    public ResponseEntity<CraneDemandResponseDto> createCraneDemand(
            @RequestBody CraneDemandCreateRequestDto craneDemandRequest, HttpServletRequest request) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userDetails = (UserDetails) authentication.getPrincipal();
        var claims = (Map<String, Object>) authentication.getDetails();

        String email = userDetails.getUsername(); // Obtiene el email
        String userId = authentication.getName(); // Obtiene el sub
        String role = claims.containsKey("role") ? claims.get("role").toString() : "USER";

        var ip = request.getHeader("X-Forwarded-For");

        if (ip == null)
            ip = request.getRemoteAddr();
        var userAgent = request.getHeader("User-Agent");
        var ahora = LocalDateTime.now();

        System.out.printf(
                "AUDITORÍA - Fecha: %s, Usuario: %s, Rol: %s, Email: %s, IP: %s, User-Agent: %s, Ubicación actual: %s, Destino: %s\n",
                ahora, userId, role, email, ip, userAgent, craneDemandRequest.getCurrentLocation(),
                craneDemandRequest.getDestinationLocation());

        var createdOwner = craneDemandService.createCraneDemand(craneDemandRequest, email);
        return new ResponseEntity<>(createdOwner, HttpStatus.CREATED);
    }

    @PutMapping("/{craneDemandId}")
    public ResponseEntity<CraneDemandResponseDto> updateCraneDemand(@PathVariable String craneDemandId,
            @RequestBody CraneDemandUpdateRequestDto CraneDemand) {
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
