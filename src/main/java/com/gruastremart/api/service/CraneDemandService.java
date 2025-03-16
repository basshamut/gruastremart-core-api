package com.gruastremart.api.service;

import com.gruastremart.api.dto.CraneDemandCreateRequestDto;
import com.gruastremart.api.dto.CraneDemandResponseDto;
import com.gruastremart.api.dto.CraneDemandUpdateRequestDto;
import com.gruastremart.api.exception.ServiceException;
import com.gruastremart.api.persistance.entity.CraneDemand;
import com.gruastremart.api.persistance.repository.CraneDemandRepository;
import com.gruastremart.api.persistance.repository.UserRepository;
import com.gruastremart.api.service.mapper.CraneDemandMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CraneDemandService {

    private final CraneDemandRepository craneDemandRepository;
    private final UserRepository userRepository;

    public Page<CraneDemandResponseDto> findWithFilters(Pageable pageable) {
        var list = craneDemandRepository.findAll(pageable).map(CraneDemandMapper.MAPPER::mapToDto);
        var countConstruction = craneDemandRepository.count();
        return new PageImpl<>(list.getContent(), pageable, countConstruction);
    }

    public CraneDemandResponseDto getCraneDemandById(String craneDemandId) {
        var craneDemand = craneDemandRepository.findById(craneDemandId);
        if (craneDemand.isEmpty()) {
            throw new ServiceException("Crane request not found", 404);
        }


        return CraneDemandMapper.MAPPER.mapToDto(craneDemand.get());
    }

    public CraneDemandResponseDto createCraneDemand(CraneDemandCreateRequestDto craneDemandCreateRequestDto) {
        var user = userRepository.findById(craneDemandCreateRequestDto.getUserId());
        if (user.isEmpty()) {
            throw new ServiceException("User not found", 404);
        }
        var craneDemandBuilded = buildCraneDemandEntityForSave(craneDemandCreateRequestDto);
        var craneDemandSaved = craneDemandRepository.save(craneDemandBuilded);
        return CraneDemandMapper.MAPPER.mapToDto(craneDemandSaved);
    }

    private CraneDemand buildCraneDemandEntityForSave(CraneDemandCreateRequestDto craneDemandCreateRequestDto) {
        var craneDemandMapped = CraneDemandMapper.MAPPER.mapToEntity(craneDemandCreateRequestDto);
        craneDemandMapped.setState("ACTIVE");
        craneDemandMapped.setDueDate(new Date());
        return craneDemandMapped;
    }

    public Optional<CraneDemandResponseDto> updateCraneDemand(String craneDemandId, CraneDemandUpdateRequestDto CraneDemand) {
        var user = craneDemandRepository.findById(craneDemandId);
        if (user.isEmpty()) {
            throw new ServiceException("Crane request not found", 404);
        }

        return user.map(craneDemand -> {
            craneDemand.setDescription(CraneDemand.getDescription());
            craneDemand.setState(CraneDemand.getState());
            CraneDemand updatedOwner = craneDemandRepository.save(craneDemand);
            return CraneDemandMapper.MAPPER.mapToDto(updatedOwner);
        });
    }

    public void deleteCraneDemand(String craneDemandId) {
        var craneDemandEntity = craneDemandRepository.findById(craneDemandId);
        if (craneDemandEntity.isEmpty()) {
            throw new ServiceException("User not found", 404);
        }
        craneDemandEntity.get().setState("INACTIVE");
        craneDemandRepository.save(craneDemandEntity.get());
    }
}
