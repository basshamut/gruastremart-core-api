package com.gruastremart.api.service;

import com.gruastremart.api.dto.CraneDemandCreateRequestDto;
import com.gruastremart.api.dto.CraneDemandResponseDto;
import com.gruastremart.api.dto.CraneDemandUpdateRequestDto;
import com.gruastremart.api.dto.UserDto;
import com.gruastremart.api.exception.ServiceException;
import com.gruastremart.api.persistance.entity.CraneDemand;
import com.gruastremart.api.persistance.repository.CraneDemandRepository;
import com.gruastremart.api.persistance.repository.UserRepository;
import com.gruastremart.api.persistance.repository.custom.CraneDemandCustomRepository;
import com.gruastremart.api.service.mapper.CraneDemandMapper;
import com.gruastremart.api.service.mapper.UserMapper;
import com.gruastremart.api.utils.tools.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CraneDemandService {

    private final CraneDemandRepository craneDemandRepository;
    private final CraneDemandCustomRepository craneDemandCustomRepository;
    private final UserRepository userRepository;

    private final SimpMessagingTemplate messagingTemplate;

    public Page<CraneDemandResponseDto> findWithFilters(MultiValueMap<String, String> params) {
        if (!PaginationUtil.isValidPagination(params.getFirst("page"), params.getFirst("size"))) {
            throw new ServiceException("Invalid pagination parameters", HttpStatus.BAD_REQUEST.value());
        }

        var pageable = Pageable
                .ofSize(Integer.parseInt(Objects.requireNonNull(params.getFirst("size"))))
                .withPage(Integer.parseInt(Objects.requireNonNull(params.getFirst("page"))));

        var list = craneDemandCustomRepository.getWithFilters(params);

        return new PageImpl<>(list.getContent().stream().map(CraneDemandMapper.MAPPER::mapToDto).toList(), pageable, list.getTotalElements());
    }

    public CraneDemandResponseDto getCraneDemandById(String craneDemandId) {
        var craneDemand = craneDemandRepository.findById(craneDemandId);
        if (craneDemand.isEmpty()) {
            throw new ServiceException("Crane request not found", 404);
        }


        return CraneDemandMapper.MAPPER.mapToDto(craneDemand.get());
    }

    public CraneDemandResponseDto createCraneDemand(CraneDemandCreateRequestDto craneDemandCreateRequestDto, String email) {
        var user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new ServiceException("User not found", 404);
        }
        var craneDemandBuilded = buildCraneDemandEntityForSave(craneDemandCreateRequestDto, user.get().getId());
        var craneDemandSaved = craneDemandRepository.save(craneDemandBuilded);
        var craneDemandSavedDto = CraneDemandMapper.MAPPER.mapToDto(craneDemandSaved);

        messagingTemplate.convertAndSend("/topic/new-demand", craneDemandSavedDto);
        
        return craneDemandSavedDto;
    }

    private CraneDemand buildCraneDemandEntityForSave(CraneDemandCreateRequestDto craneDemandCreateRequestDto, String userId) {
        var craneDemandMapped = CraneDemandMapper.MAPPER.mapToEntity(craneDemandCreateRequestDto);
        craneDemandMapped.setUserId(userId);
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
