package com.gruastremart.api.persistance.repository.custom;

import com.gruastremart.api.exception.ServiceException;
import com.gruastremart.api.persistance.entity.CranePricing;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.MultiValueMap;

import java.util.Objects;

@RequiredArgsConstructor
@Repository
@Slf4j
public class CranePricingCustomRepository {
    public static final int DEFAULT_PAGE_VALUE = 0;
    public static final int DEFAULT_SIZE_VALUE = 10;

    private final MongoTemplate mongoTemplate;

    public Page<CranePricing> getWithFilters(MultiValueMap<String, String> params) {
        var page = params.containsKey("page") ? Integer.parseInt(Objects.requireNonNull(params.getFirst("page"))) : DEFAULT_PAGE_VALUE;
        var size = params.containsKey("size") ? Integer.parseInt(Objects.requireNonNull(params.getFirst("size"))) : DEFAULT_SIZE_VALUE;
        var pageable = Pageable.ofSize(size).withPage(page);

        var query = new Query();

        applyActiveFilter(query, params);
        applyWeightFilter(query, params);
        applyWeightCategoryFilter(query, params);
        applyPricingTypeFilter(query, params);
        applyUrbanPriceRangeFilters(query, params);
        applyExtraUrbanBasePriceFilter(query, params);

        query.with(Sort.by(Sort.Direction.DESC, "createdAt"));
        query.with(pageable);

        var pricingList = mongoTemplate.find(query, CranePricing.class);
        var count = mongoTemplate.count(query.skip(DEFAULT_PAGE_VALUE).limit(DEFAULT_PAGE_VALUE), CranePricing.class);

        return new PageImpl<>(pricingList, pageable, count);
    }

    private void applyActiveFilter(Query query, MultiValueMap<String, String> params) {
        if (params.containsKey("active")) {
            boolean isActive = Boolean.parseBoolean(params.getFirst("active"));
            query.addCriteria(Criteria.where("active").is(isActive));
        }
    }

    private void applyWeightFilter(Query query, MultiValueMap<String, String> params) {
        if (params.containsKey("weight")) {
            String weightParam = params.getFirst("weight");

            if (weightParam == null || weightParam.trim().isEmpty()) {
                log.warn("Weight parameter is empty or null");
                throw new ServiceException("Weight parameter is required", 400);
            }

            try {
                Integer weight = Integer.parseInt(weightParam);
                query.addCriteria(Criteria.where("weightCategory.minWeightKg").lte(weight)
                        .and("weightCategory.maxWeightKg").gte(weight));
            } catch (NumberFormatException e) {
                log.warn("Invalid weight format: {}", weightParam);
                throw new ServiceException("Invalid weight format: " + weightParam, 400);
            }
        }
    }

    private void applyWeightCategoryFilter(Query query, MultiValueMap<String, String> params) {
        if (params.containsKey("weightCategory")) {
            String categoryName = params.getFirst("weightCategory");
            if (categoryName != null && !categoryName.trim().isEmpty()) {
                query.addCriteria(Criteria.where("weightCategory.name").regex(categoryName, "i"));
            }
        }
    }

    private void applyPricingTypeFilter(Query query, MultiValueMap<String, String> params) {
        if (params.containsKey("pricingType")) {
            String pricingType = params.getFirst("pricingType");
            if (pricingType != null && !pricingType.trim().isEmpty()) {
                Criteria typeCriteria = new Criteria().orOperator(
                        Criteria.where("pricing.urban.type").regex(pricingType, "i"),
                        Criteria.where("pricing.extraUrban.type").regex(pricingType, "i")
                );
                query.addCriteria(typeCriteria);
            }
        }
    }

    private void applyUrbanPriceRangeFilters(Query query, MultiValueMap<String, String> params) {
        applyMinUrbanPriceFilter(query, params);
        applyMaxUrbanPriceFilter(query, params);
    }

    private void applyMinUrbanPriceFilter(Query query, MultiValueMap<String, String> params) {
        if (params.containsKey("minUrbanPrice")) {
            String minPriceParam = params.getFirst("minUrbanPrice");

            if (minPriceParam == null || minPriceParam.trim().isEmpty()) {
                log.warn("MinUrbanPrice parameter is empty or null");
                throw new ServiceException("MinUrbanPrice parameter is required", 400);
            }

            try {
                Double minPrice = Double.parseDouble(minPriceParam);
                query.addCriteria(Criteria.where("pricing.urban.fixedPriceUsd").gte(minPrice));
            } catch (NumberFormatException e) {
                log.warn("Invalid minUrbanPrice format: {}", minPriceParam);
                throw new ServiceException("Invalid minUrbanPrice format: " + minPriceParam, 400);
            }
        }
    }

    private void applyMaxUrbanPriceFilter(Query query, MultiValueMap<String, String> params) {
        if (params.containsKey("maxUrbanPrice")) {
            String maxPriceParam = params.getFirst("maxUrbanPrice");

            if (maxPriceParam == null || maxPriceParam.trim().isEmpty()) {
                log.warn("MaxUrbanPrice parameter is empty or null");
                throw new ServiceException("MaxUrbanPrice parameter is required", 400);
            }

            try {
                Double maxPrice = Double.parseDouble(maxPriceParam);
                query.addCriteria(Criteria.where("pricing.urban.fixedPriceUsd").lte(maxPrice));
            } catch (NumberFormatException e) {
                log.warn("Invalid maxUrbanPrice format: {}", maxPriceParam);
                throw new ServiceException("Invalid maxUrbanPrice format: " + maxPriceParam, 400);
            }
        }
    }

    private void applyExtraUrbanBasePriceFilter(Query query, MultiValueMap<String, String> params) {
        if (params.containsKey("minExtraUrbanBasePrice")) {
            String minBasePriceParam = params.getFirst("minExtraUrbanBasePrice");

            if (minBasePriceParam == null || minBasePriceParam.trim().isEmpty()) {
                log.warn("MinExtraUrbanBasePrice parameter is empty or null");
                throw new ServiceException("MinExtraUrbanBasePrice parameter is required", 400);
            }

            try {
                Double minBasePrice = Double.parseDouble(minBasePriceParam);
                query.addCriteria(Criteria.where("pricing.extraUrban.basePriceUsd").gte(minBasePrice));
            } catch (NumberFormatException e) {
                log.warn("Invalid minExtraUrbanBasePrice format: {}", minBasePriceParam);
                throw new ServiceException("Invalid minExtraUrbanBasePrice format: " + minBasePriceParam, 400);
            }
        }
    }
}
