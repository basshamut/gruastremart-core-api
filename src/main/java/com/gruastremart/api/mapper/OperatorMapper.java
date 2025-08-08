package com.gruastremart.api.mapper;

import com.gruastremart.api.dto.OperatorDto;
import com.gruastremart.api.persistance.entity.Operator;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OperatorMapper {
    OperatorMapper MAPPER = Mappers.getMapper(OperatorMapper.class);

    OperatorDto mapToDto(Operator entity);
}
