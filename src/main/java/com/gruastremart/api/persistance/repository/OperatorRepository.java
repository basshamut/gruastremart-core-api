package com.gruastremart.api.persistance.repository;

import com.gruastremart.api.persistance.entity.Operator;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OperatorRepository extends MongoRepository<Operator, String> {

    @Query("{ 'userId' : ?0 }")
    Optional<Operator> findByUserId(String userId);
}
