package com.gruastremart.api.persistance.repository;

import com.gruastremart.api.persistance.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {

    /**
     * Buscar pagos por ID de usuario con paginación
     */
    Page<Payment> findByUserId(String userId, Pageable pageable);

    /**
     * Buscar pago por ID de demanda
     */
    Optional<Payment> findByDemandId(String demandId);

    /**
     * Buscar pagos por estado con paginación
     */
    Page<Payment> findByStatus(String status, Pageable pageable);

    /**
     * Buscar pagos por usuario y estado
     */
    Page<Payment> findByUserIdAndStatus(String userId, String status, Pageable pageable);

    /**
     * Buscar pagos por lista de IDs de demanda
     */
    List<Payment> findByDemandIdIn(List<String> demandIds);

    /**
     * Verificar si existe un pago para una demanda
     */
    boolean existsByDemandId(String demandId);
}
