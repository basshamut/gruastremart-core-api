package com.gruastremart.api.persistance.repository.custom;

import com.gruastremart.api.persistance.entity.CraneDemand;
import lombok.RequiredArgsConstructor;
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

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Repository
public class CraneDemandCustomRepository {
    private final MongoTemplate mongoTemplate;

    public Page<CraneDemand> getWithFilters(MultiValueMap<String, String> params) {

        var page = params.containsKey("page") ? Integer.parseInt(Objects.requireNonNull(params.getFirst("page"))) : 0; // Valor por defecto
        var size = params.containsKey("size") ? Integer.parseInt(Objects.requireNonNull(params.getFirst("size"))) : 10; // Valor por defecto
        var lat = params.containsKey("lat") ? Double.parseDouble(Objects.requireNonNull(params.getFirst("lat"))) : 0.0;
        var lng = params.containsKey("lng") ? Double.parseDouble(Objects.requireNonNull(params.getFirst("lng"))) : 0.0;
        var radio = params.containsKey("radio") ? Double.parseDouble(Objects.requireNonNull(params.getFirst("radio"))) : 0.0; // Radio por defecto (ej: 10 km)

        var pageable = Pageable.ofSize(size).withPage(page);

        var query = new Query();

        if (lat != 0 && lng != 0 && radio != 0) {
            var punto = new Point(lng, lat); // GeoJSON usa (lng, lat)
            var distancia = new Distance(radio / 1000.0, Metrics.KILOMETERS); // Mongo usa kilómetros

            query.addCriteria(Criteria.where("currentLocation")
                    .nearSphere(punto)
                    .maxDistance(distancia.getNormalizedValue()));
        }

        query.with(Sort.by(Sort.Direction.DESC, "dueDate"));
        query.with(pageable);

        var demands = mongoTemplate.find(query, CraneDemand.class);
        var count = mongoTemplate.count(query.skip(0).limit(0), CraneDemand.class);

        return new PageImpl<>(demands, pageable, count);
    }
}
