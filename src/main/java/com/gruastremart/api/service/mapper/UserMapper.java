package com.gruastremart.api.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.gruastremart.api.dto.UserDto;
import com.gruastremart.api.persistance.entity.User;

@Mapper(componentModel = "spring", imports = {User.class, UserDto.class})
public interface UserMapper {
    UserMapper MAPPER = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    UserDto mapToDto(User entity);

    User mapToEntity(UserDto dto);
    
}
