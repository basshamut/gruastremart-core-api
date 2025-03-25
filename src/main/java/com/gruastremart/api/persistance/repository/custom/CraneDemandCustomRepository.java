package com.gruastremart.api.persistance.repository.custom;

import com.gruastremart.api.persistance.entity.CraneDemand;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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

        int page = Integer.parseInt(Objects.requireNonNull(params.getFirst("page")));
        int size = Integer.parseInt(Objects.requireNonNull(params.getFirst("size")));
        Pageable pageable = Pageable.ofSize(size).withPage(page);

        // Build the query
        Query query = new Query();
        if (params.containsKey("role")) {
            query.addCriteria(Criteria.where("role").is(params.getFirst("role")));
        }
        if (params.containsKey("email")) {
            query.addCriteria(Criteria.where("email").regex(Objects.requireNonNull(params.getFirst("email")), "i"));
        }

        query.with(pageable);

        List<CraneDemand> accounts = mongoTemplate.find(query, CraneDemand.class);
        long count = mongoTemplate.count(query.skip(0).limit(0), CraneDemand.class);

        return new PageImpl<>(accounts, pageable, count);
    }
}
