package com.gruastremart.api.service;

import com.gruastremart.api.dto.OperatorLocationDto;
import com.gruastremart.api.dto.OperatorLocationRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.gruastremart.api.utils.constants.Constants.OPERATOR_LOCATIONS_CACHE;

@Slf4j
@Service
@RequiredArgsConstructor
public class OperatorService {

    @CachePut(value = OPERATOR_LOCATIONS_CACHE, key = "#operatorId", cacheManager = "operatorLocationsCacheManager")
    public OperatorLocationDto saveOperatorLocation(String operatorId, OperatorLocationRequestDto request) {
        log.debug("Guardando localización del operador: {}", operatorId);

        OperatorLocationDto location = OperatorLocationDto.builder()
                .operatorId(operatorId)
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .status(request.getStatus() != null ? request.getStatus() : "ONLINE")
                .timestamp(LocalDateTime.now())
                .build();

        log.debug("Localización guardada en cache para operador: {} - Lat: {}, Lng: {}",
                operatorId, location.getLatitude(), location.getLongitude());

        return location;
    }

    @Cacheable(value = OPERATOR_LOCATIONS_CACHE, key = "#operatorId", cacheManager = "operatorLocationsCacheManager")
    public Optional<OperatorLocationDto> getOperatorLocation(String operatorId) {
        log.debug("Buscando localización del operador en cache: {}", operatorId);

        return Optional.empty();
    }

    public boolean isOperatorLocationCached(String operatorId) {
        return getOperatorLocation(operatorId).isPresent();
    }
}
