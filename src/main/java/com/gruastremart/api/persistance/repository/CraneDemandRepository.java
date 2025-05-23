package com.gruastremart.api.persistance.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.gruastremart.api.persistance.entity.CraneDemand;

import java.util.List;
import java.util.Optional;

@Repository
public interface CraneDemandRepository extends MongoRepository<CraneDemand, String> {
    List<CraneDemand> findByCreatedByUserId(String id);
}
