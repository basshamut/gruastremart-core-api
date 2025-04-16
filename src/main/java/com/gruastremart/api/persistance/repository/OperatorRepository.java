package com.gruastremart.api.persistance.repository;

import com.gruastremart.api.persistance.entity.CraneOperator;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OperatorRepository extends MongoRepository<CraneOperator, String> {
    Optional<CraneOperator> findByUserId(String id);
}
