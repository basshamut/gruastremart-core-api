package com.gruastremart.api.persistance.repository.custom;

import com.gruastremart.api.persistance.entity.User;
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
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Repository
public class UserCustomRepository {
    private final MongoTemplate mongoTemplate;

    public Page<User> getWithFilters(MultiValueMap<String, String> params) {

        int page = Integer.parseInt(Objects.requireNonNull(params.getFirst("page")));
        int size = Integer.parseInt(Objects.requireNonNull(params.getFirst("size")));
        Pageable pageable = Pageable.ofSize(size).withPage(page);

        Query query = new Query();
        if (params.containsKey("role")) {
            query.addCriteria(Criteria.where("role").is(params.getFirst("role")));
        }
        if (params.containsKey("email")) {
            String emailParam = Objects.requireNonNull(params.getFirst("email"));
            String escapedEmail = Pattern.quote(emailParam);
            query.addCriteria(Criteria.where("email").regex(escapedEmail, "i"));
        }
        if(params.containsKey("supabaseId")) {
            query.addCriteria(Criteria.where("supabaseId").is(params.getFirst("supabaseId")));
        }
        if (params.containsKey("active")) {
            boolean active = Boolean.parseBoolean(Objects.requireNonNull(params.getFirst("active")));
            query.addCriteria(Criteria.where("active").is(active));
        }

        query.with(pageable);

        List<User> accounts = mongoTemplate.find(query, User.class);
        long count = mongoTemplate.count(query.skip(0).limit(0), User.class);

        return new PageImpl<>(accounts, pageable, count);
    }
}
