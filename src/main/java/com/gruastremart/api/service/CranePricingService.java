package com.gruastremart.api.service;

import com.gruastremart.api.dto.CranePricingResponseDto;
import com.gruastremart.api.exception.ServiceException;
import com.gruastremart.api.mapper.CranePricingMapper;
import com.gruastremart.api.persistance.entity.CranePricing;
import com.gruastremart.api.persistance.repository.CranePricingRepository;
import com.gruastremart.api.persistance.repository.custom.CranePricingCustomRepository;
import com.gruastremart.api.utils.tools.PaginationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.util.Optional;

import static com.gruastremart.api.utils.constants.Constants.CRANE_PRICING_CACHE;

@Service
@RequiredArgsConstructor
@Slf4j
public class CranePricingService {

    private final CranePricingRepository cranePricingRepository;
    private final CranePricingCustomRepository cranePricingCustomRepository;
    private final CranePricingMapper cranePricingMapper;

    @Cacheable(value = CRANE_PRICING_CACHE,
               key = "'findWithFilters:' + T(java.util.Objects).hash(#params.getFirst('page'), #params.getFirst('size'), #params.getFirst('active'), #params.getFirst('weight'), #params.getFirst('weightCategory'), #params.getFirst('pricingType'), #params.getFirst('minUrbanPrice'), #params.getFirst('maxUrbanPrice'), #params.getFirst('minExtraUrbanBasePrice'))",
               cacheManager = "cranePricingCacheManager")
    public Page<CranePricingResponseDto> findWithFilters(MultiValueMap<String, String> params) {
        if (PaginationUtil.isValidPagination(params.getFirst("page"), params.getFirst("size"))) {
            throw new ServiceException("Invalid pagination parameters", HttpStatus.BAD_REQUEST.value());
        }

        log.info("Executing database query for crane pricing with filters: {}", params);

        Page<CranePricing> pricingPage = cranePricingCustomRepository.getWithFilters(params);
        var responseDtos = cranePricingMapper.mapToDto(pricingPage.getContent());
        return new PageImpl<>(responseDtos, pricingPage.getPageable(), pricingPage.getTotalElements());
    }

    @Cacheable(value = CRANE_PRICING_CACHE, key = "'findById:' + #id",
               cacheManager = "cranePricingCacheManager")
    public CranePricingResponseDto findById(String id) {
        log.info("Finding crane pricing by id: {} - CACHE MISS", id);
        Optional<CranePricing> pricing = cranePricingRepository.findById(id);

        if (pricing.isEmpty()) {
            throw new ServiceException("Crane pricing not found with id: " + id, HttpStatus.NOT_FOUND.value());
        }

        return cranePricingMapper.mapToDto(pricing.get());
    }
}
