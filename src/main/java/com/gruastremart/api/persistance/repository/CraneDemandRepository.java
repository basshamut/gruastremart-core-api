package com.gruastremart.api.persistance.repository;

import com.gruastremart.api.persistance.entity.CraneRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CraneDemandRepository extends MongoRepository<CraneRequest, String> {
}

