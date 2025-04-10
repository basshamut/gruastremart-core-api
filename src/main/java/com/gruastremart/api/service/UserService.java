package com.gruastremart.api.service;

import com.gruastremart.api.dto.UserDto;
import com.gruastremart.api.exception.ServiceException;
import com.gruastremart.api.persistance.repository.UserRepository;
import com.gruastremart.api.persistance.repository.custom.UserCustomRepository;
import com.gruastremart.api.service.mapper.UserMapper;
import com.gruastremart.api.utils.tools.PaginationUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.util.MultiValueMap;

import java.util.Objects;

@Repository
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserCustomRepository userCustomRepository;

    public UserDto register(UserDto userDto) {
        var user = userRepository.findByEmail(userDto.getEmail());

        if (user.isPresent()) {
            throw new ServiceException("User already exists", 400);
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

    public Page<UserDto> findWithFilters(MultiValueMap<String, String> params) {
        if (PaginationUtil.isValidPagination(params.getFirst("page"), params.getFirst("size"))) {
            throw new ServiceException("Invalid pagination parameters", HttpStatus.BAD_REQUEST.value());
        }

        var pageable = Pageable
                .ofSize(Integer.parseInt(Objects.requireNonNull(params.getFirst("size"))))
                .withPage(Integer.parseInt(Objects.requireNonNull(params.getFirst("page"))));

        var list = userCustomRepository.getWithFilters(params);

        return new PageImpl<>(list.getContent().stream().map(UserMapper.MAPPER::mapToDto).toList(), pageable, list.getTotalElements());
    }
}
