package com.gruastremart.api.service.mapper;

import com.gruastremart.api.dto.CraneDemandCreateRequestDto;
import com.gruastremart.api.dto.CraneDemandResponseDto;
import com.gruastremart.api.persistance.entity.CraneDemand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", imports = {CraneDemand.class, CraneDemandCreateRequestDto.class})
public interface CraneDemandMapper {
    CraneDemandMapper MAPPER = Mappers.getMapper(CraneDemandMapper.class);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "userId", source = "userId")
    @Mapping(source = "dueDate", target = "dueDate", dateFormat = "dd/MM/yyyy")
    @Mapping(target = "state", source = "state")
    CraneDemandResponseDto mapToDto(CraneDemand entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "description", source = "description")
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "dueDate", ignore = true)
    @Mapping(target = "state", ignore = true)
    CraneDemand mapToEntity(CraneDemandCreateRequestDto dto);

}
