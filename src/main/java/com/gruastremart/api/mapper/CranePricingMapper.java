package com.gruastremart.api.mapper;

import com.gruastremart.api.dto.CranePricingResponseDto;
import com.gruastremart.api.dto.WeightCategoryDto;
import com.gruastremart.api.dto.PricingDto;
import com.gruastremart.api.dto.UrbanPricingDto;
import com.gruastremart.api.dto.ExtraUrbanPricingDto;
import com.gruastremart.api.persistance.entity.CranePricing;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CranePricingMapper {
    CranePricingMapper MAPPER = Mappers.getMapper(CranePricingMapper.class);

    @Mapping(target = "weightCategory", source = "weightCategory")
    @Mapping(target = "pricing", source = "pricing")
    CranePricingResponseDto mapToDto(CranePricing entity);

    List<CranePricingResponseDto> mapToDto(List<CranePricing> entities);

    WeightCategoryDto mapWeightCategoryToDto(CranePricing.WeightCategory weightCategory);

    @Mapping(target = "urban", source = "urban")
    @Mapping(target = "extraUrban", source = "extraUrban")
    PricingDto mapPricingToDto(CranePricing.Pricing pricing);

    UrbanPricingDto mapUrbanPricingToDto(CranePricing.UrbanPricing urbanPricing);

    ExtraUrbanPricingDto mapExtraUrbanPricingToDto(CranePricing.ExtraUrbanPricing extraUrbanPricing);
}
