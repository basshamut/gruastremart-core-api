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
        return UserMapper.MAPPER.mapToDto(user);
    }

    public UserDto register(UserDto userDto) {
        var user = userRepository.findByEmail(userDto.getEmail());

        if (user.isPresent()) {
            throw new ServiceException("Operator already exists", 400);
        }

        var operator = UserMapper.MAPPER.mapToEntity(userDto);
        operator = userRepository.save(operator);
        return UserMapper.MAPPER.mapToDto(operator);
    }

    public UserDto update(String id, UserDto userDto) {
        var user = userRepository.findById(id).orElseThrow(() -> new ServiceException("User not found", 404));

        user.setEmail(userDto.getEmail());
        user.setName(userDto.getName());
        user.setLastName(userDto.getLastName());
        user.setPhone(userDto.getPhone());
        user.setAddress(userDto.getAddress());
        user.setIdentificationNumber(userDto.getIdentificationNumber());
        user.setBirthDate(userDto.getBirthDate());
        user.setRole(userDto.getRole());

        user = UserMapper.MAPPER.mapToEntity(userDto);
        user = userRepository.save(user);
        return UserMapper.MAPPER.mapToDto(user);
    }
}
