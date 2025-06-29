package com.gruastremart.api.persistance.repository;

import com.gruastremart.api.persistance.entity.CraneDemand;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CraneDemandRepository extends MongoRepository<CraneDemand, String> {
    List<CraneDemand> findByCreatedByUserId(String id);

    List<CraneDemand> findByState(String name);
}
