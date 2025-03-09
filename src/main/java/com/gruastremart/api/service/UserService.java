package com.gruastremart.api.service;

import org.springframework.stereotype.Repository;

import com.gruastremart.api.dto.UserDto;
import com.gruastremart.api.exception.ServiceException;
import com.gruastremart.api.persistance.repository.UserRepository;
import com.gruastremart.api.service.mapper.UserMapper;

import lombok.AllArgsConstructor;

@Repository
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDto findUserByEmail(String userId) {
        var user = userRepository.findByEmail(userId).orElseThrow(() -> new ServiceException("User not found", 404));
        var userMapped = UserMapper.MAPPER.mapToDto(user);
        return userMapped;
    }
}
