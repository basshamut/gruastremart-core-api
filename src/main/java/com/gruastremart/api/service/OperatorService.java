package com.gruastremart.api.service;

import com.gruastremart.api.dto.OperatorDto;
import com.gruastremart.api.dto.OperatorLocationDto;
import com.gruastremart.api.dto.OperatorLocationRequestDto;
import com.gruastremart.api.exception.ServiceException;
import com.gruastremart.api.mapper.OperatorMapper;
import com.gruastremart.api.persistance.repository.OperatorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.gruastremart.api.utils.constants.Constants.OPERATOR_LOCATIONS_CACHE;

@Slf4j
@Service
@RequiredArgsConstructor
public class OperatorService {

    private final OperatorRepository operatorRepository;
    private final CacheManager cacheManager;

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

    public Optional<OperatorLocationDto> getOperatorLocation(String operatorId) {
        log.debug("Buscando localización del operador en cache: {}", operatorId);
        
        var cache = cacheManager.getCache(OPERATOR_LOCATIONS_CACHE);
        if (cache == null) {
            return Optional.empty();
        }
        
        var wrapper = cache.get(operatorId);
        if (wrapper != null) {
            return Optional.of((OperatorLocationDto) wrapper.get());
        }
        
        return Optional.empty();
    }

    public boolean isOperatorLocationCached(String operatorId) {
        return getOperatorLocation(operatorId).isPresent();
    }

    public OperatorDto findByUserId(String userId) {
        var operator = operatorRepository.findByUserId(userId).orElseThrow(() -> new ServiceException("User not found", 404));
        return OperatorMapper.MAPPER.mapToDto(operator);
    }
}
