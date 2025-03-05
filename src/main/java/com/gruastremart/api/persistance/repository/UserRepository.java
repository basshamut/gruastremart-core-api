package com.gruastremart.api.persistance.repository;

import com.gruastremart.api.persistance.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    User findByEmail(String email);
}
