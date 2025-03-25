package com.gruastremart.api.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.gruastremart.api.dto.UserDto;
import com.gruastremart.api.persistance.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper MAPPER = Mappers.getMapper(UserMapper.class);

    UserDto mapToDto(User entity);

    @Mapping(target = "id", ignore = true)
    User mapToEntity(UserDto dto);
    
}
