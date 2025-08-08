package com.gruastremart.api.persistance.repository;

import com.gruastremart.api.persistance.entity.CranePricing;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CranePricingRepository extends MongoRepository<CranePricing, String> {
}
