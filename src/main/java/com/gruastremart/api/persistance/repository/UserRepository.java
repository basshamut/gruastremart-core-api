package com.gruastremart.api.persistance.repository;

import com.gruastremart.api.persistance.entity.User;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findBySupabaseId(String supabaseId);
}
