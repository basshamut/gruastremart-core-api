package com.gruastremart.api.persistance.repository.custom;

import com.gruastremart.api.persistance.entity.CraneDemand;
import com.gruastremart.api.utils.enums.CraneDemandStateEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

@RequiredArgsConstructor
@Repository
@Slf4j
public class CraneDemandCustomRepository {
    public static final int DEFAULT_PAGE_VALUE = 0;
    public static final int DEFAULT_SIZE_VALUE = 10;
    public static final double DEFAULT_LATITUDE_VALUE = 0.0;
    public static final double DEFAULT_LONGITUDE_VALUE = 0.0;
    public static final double DEFAULT_RADIO_VALUE_IN_KM = 5.0;
    private final MongoTemplate mongoTemplate;

    public Page<CraneDemand> getWithFilters(MultiValueMap<String, String> params) {

        var page = params.containsKey("page") ? Integer.parseInt(Objects.requireNonNull(params.getFirst("page"))) : DEFAULT_PAGE_VALUE;
        var size = params.containsKey("size") ? Integer.parseInt(Objects.requireNonNull(params.getFirst("size"))) : DEFAULT_SIZE_VALUE;
        var lat = params.containsKey("lat") ? Double.parseDouble(Objects.requireNonNull(params.getFirst("lat"))) : DEFAULT_LATITUDE_VALUE;
        var lng = params.containsKey("lng") ? Double.parseDouble(Objects.requireNonNull(params.getFirst("lng"))) : DEFAULT_LONGITUDE_VALUE;
        var radio = params.containsKey("radio") ? Double.parseDouble(Objects.requireNonNull(params.getFirst("radio"))) : DEFAULT_RADIO_VALUE_IN_KM;
        var pageable = Pageable.ofSize(size).withPage(page);

        var query = new Query();
        if (params.containsKey("state")) {
            var state = CraneDemandStateEnum.valueOf(params.getFirst("state")).name();
            query.addCriteria(Criteria.where("state").is(state));
        }

        if (params.containsKey("createdByUserId")) {
            query.addCriteria(Criteria.where("createdByUserId").is(params.getFirst("createdByUserId")));
        }

        if (lat != DEFAULT_PAGE_VALUE && lng != DEFAULT_PAGE_VALUE && radio != DEFAULT_PAGE_VALUE) {
            var punto = new Point(lng, lat); // GeoJSON usa (lng, lat)
            var distancia = new Distance(radio, Metrics.KILOMETERS);

            query.addCriteria(Criteria.where("currentLocation")
                    .nearSphere(punto)
                    .maxDistance(distancia.getNormalizedValue()));
        }

        // Filtros por rango de fechas
        if (params.containsKey("startDate") || params.containsKey("endDate")) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            try {
                Criteria dateCriteria = Criteria.where("createdAt");

                if (params.containsKey("startDate")) {
                    String startDateStr = params.getFirst("startDate");
                    if (startDateStr != null && !startDateStr.trim().isEmpty()) {
                        LocalDate startDate = LocalDate.parse(startDateStr, formatter);
                        dateCriteria = dateCriteria.gte(startDate.atStartOfDay());
                    }
                }

                if (params.containsKey("endDate")) {
                    String endDateStr = params.getFirst("endDate");
                    if (endDateStr != null && !endDateStr.trim().isEmpty()) {
                        LocalDate endDate = LocalDate.parse(endDateStr, formatter);
                        dateCriteria = dateCriteria.lte(endDate.atTime(23, 59, 59));
                    }
                }

                query.addCriteria(dateCriteria);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid date format. Use 'yyyy-MM-dd'.", e);
            }
        }

        query.with(Sort.by(Sort.Direction.DESC, "createdAt"));
        query.with(pageable);

        var demands = mongoTemplate.find(query, CraneDemand.class);
        var count = mongoTemplate.count(query.skip(DEFAULT_PAGE_VALUE).limit(DEFAULT_PAGE_VALUE), CraneDemand.class);

        return new PageImpl<>(demands, pageable, count);
    }
}

