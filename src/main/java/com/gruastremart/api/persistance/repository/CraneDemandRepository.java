package com.gruastremart.api.persistance.repository;

import com.gruastremart.api.persistance.entity.CraneDemand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CraneDemandRepository extends MongoRepository<CraneDemand, String> {
    List<CraneDemand> findByCreatedByUserId(String id);

    @Query("{ 'assignedOperatorId': ?0, 'state': 'TAKEN' }")
    Optional<CraneDemand> hasOperatorAssignedAndIsTaken(String operatorId);

    /**
     * Busca demandas asignadas a un operador con un estado específico
     */
    Page<CraneDemand> findByAssignedOperatorIdAndState(String operatorId, String state, Pageable pageable);
}
