package com.gruastremart.api.mapper;

import com.gruastremart.api.dto.CraneDemandCreateRequestDto;
import com.gruastremart.api.dto.CraneDemandResponseDto;
import com.gruastremart.api.dto.LocationDto;
import com.gruastremart.api.persistance.entity.CraneDemand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.util.Date;

@Mapper(componentModel = "spring", imports = {GeoJsonPoint.class, Date.class, LocationDto.class, CraneDemand.class, CraneDemandResponseDto.class})
public interface CraneDemandMapper {

    CraneDemandMapper MAPPER = org.mapstruct.factory.Mappers.getMapper(CraneDemandMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "currentLocationName", source = "currentLocation.name")
    @Mapping(target = "currentLocationAccuracy", source = "currentLocation.accuracy")
    @Mapping(target = "destinationLocationName", source = "destinationLocation.name")
    @Mapping(target = "destinationLocationAccuracy", source = "destinationLocation.accuracy")
    @Mapping(target = "currentLocation",  expression = "java(mapToLocation(dto.getCurrentLocation()))")
    @Mapping(target = "destinationLocation", expression = "java(mapToLocation(dto.getDestinationLocation()))")
    CraneDemand mapToEntity(CraneDemandCreateRequestDto dto);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "assignedOperatorId", source = "entity.assignedOperatorId")
    @Mapping(source = "entity.createdAt", target = "createdAt", dateFormat = "dd/MM/yyyy")
    @Mapping(target = "currentLocation", expression = "java(mapToLocationDto(entity.getCurrentLocation(), entity.getCurrentLocationName(), entity.getCurrentLocationAccuracy()))")
    @Mapping(target = "destinationLocation", expression = "java(mapToLocationDto(entity.getDestinationLocation(), entity.getDestinationLocationName(), entity.getDestinationLocationAccuracy()))")
    CraneDemandResponseDto mapToDto(CraneDemand entity);

    default GeoJsonPoint mapToLocation(LocationDto locationDto) {
        if (locationDto == null || locationDto.getLongitude() == null || locationDto.getLatitude() == null) {
            return null;
        }
        return new GeoJsonPoint(locationDto.getLongitude(), locationDto.getLatitude());
    }

    default LocationDto mapToLocationDto(GeoJsonPoint geoJsonPoint, String name, Double accuracy) {
        if (geoJsonPoint == null) {
            return null;
        }
        return LocationDto.builder()
                .longitude(geoJsonPoint.getX())
                .latitude(geoJsonPoint.getY())
                .name(name)
                .accuracy(accuracy)
                .build();
    }
}