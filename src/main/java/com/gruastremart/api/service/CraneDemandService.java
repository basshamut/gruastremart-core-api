package com.gruastremart.api.service;

import com.gruastremart.api.dto.AssignCraneDemandDto;
import com.gruastremart.api.dto.CraneDemandCreateRequestDto;
import com.gruastremart.api.dto.CraneDemandResponseDto;
import com.gruastremart.api.dto.OperatorLocationRequestDto;
import com.gruastremart.api.exception.ServiceException;
import com.gruastremart.api.mapper.CraneDemandMapper;
import com.gruastremart.api.persistance.entity.CraneDemand;
import com.gruastremart.api.persistance.entity.User;
import com.gruastremart.api.persistance.repository.CraneDemandRepository;
import com.gruastremart.api.persistance.repository.UserRepository;
import com.gruastremart.api.persistance.repository.custom.CraneDemandCustomRepository;
import com.gruastremart.api.utils.enums.CraneDemandStateEnum;
import com.gruastremart.api.utils.tools.PaginationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CraneDemandService {

    private final CraneDemandRepository craneDemandRepository;
    private final CraneDemandCustomRepository craneDemandCustomRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final OperatorService operatorService;

    public Page<CraneDemandResponseDto> findWithFilters(MultiValueMap<String, String> params) {
        if (PaginationUtil.isValidPagination(params.getFirst("page"), params.getFirst("size"))) {
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

    public CraneDemandResponseDto createCraneDemand(CraneDemandCreateRequestDto dto, String email) {
        var user = getUserByEmail(email);

        validateUserHasNoActiveDemand(user.getId());

        var craneDemand = buildCraneDemandEntityForSave(dto, user.getId());
        var saved = craneDemandRepository.save(craneDemand);

        return CraneDemandMapper.MAPPER.mapToDto(saved);
    }

    private void validateUserHasNoActiveDemand(String userId) {
        var demands = craneDemandRepository.findByCreatedByUserId(userId);
        if (demands.stream().anyMatch(CraneDemand::isActiveOrTaken)) {
            throw new ServiceException("User already has an active or taken crane demand", 400);
        }
    }

    private CraneDemand buildCraneDemandEntityForSave(CraneDemandCreateRequestDto craneDemandCreateRequestDto, String userId) {
        var craneDemandMapped = CraneDemandMapper.MAPPER.mapToEntity(craneDemandCreateRequestDto);
        craneDemandMapped.setCreatedByUserId(userId);
        craneDemandMapped.setState("ACTIVE");
        craneDemandMapped.setCreatedAt(new Date());
        return craneDemandMapped;
    }

    public Optional<CraneDemandResponseDto> assignCraneDemand(String craneDemandId, AssignCraneDemandDto assignCraneDemandDto) {

        var craneDemandExistance = craneDemandRepository.hasOperatorAssignedAndIsTaken(assignCraneDemandDto.getUserId());
        if (craneDemandExistance.isPresent()) {
            throw new ServiceException("User already has an active or taken crane demand", 400);
        }

        var craneDemand = getCreaneDemandById(craneDemandId);
        var userThatTakeDemand = getUserById(assignCraneDemandDto.getUserId());
        var userThatCreateDemand = getUserById(craneDemand.getCreatedByUserId());
        var updated = updateCraneDemandWithOperatorInformation(craneDemand, userThatTakeDemand, assignCraneDemandDto);

        initializeOperatorLocationInCache(userThatTakeDemand, assignCraneDemandDto);
        sendEmailToUserThatCreateDemand(userThatCreateDemand);

        return Optional.of(CraneDemandMapper.MAPPER.mapToDto(updated));
    }

    private void sendEmailToUserThatCreateDemand(User userThatCreateDemand) {
        emailService.sendResponseOfCraneDemandEmail(userThatCreateDemand.getName(), userThatCreateDemand.getEmail());
    }

    private CraneDemand getCreaneDemandById(String craneDemandId) {
        var craneDemand = craneDemandRepository.findById(craneDemandId);
        if (craneDemand.isEmpty()) {
            throw new ServiceException("Crane request not found", 404);
        }
        return craneDemand.get();
    }

    private User getUserById(String userId) {
        var user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new ServiceException("User not found", 404);
        }

        return user.get();
    }

    private User getUserByEmail(String email) {
        var user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new ServiceException("User not found", 404);
        }

        return user.get();
    }

    private CraneDemand updateCraneDemandWithOperatorInformation(CraneDemand craneDemand, User user, AssignCraneDemandDto assignCraneDemandDto) {
        craneDemand.setEditedByUserId(user.getId());
        craneDemand.setAssignedOperatorId(user.getId());
        craneDemand.setAssignedWeightCategoryId(assignCraneDemandDto.getWeightCategory().getId());
        craneDemand.setState(CraneDemandStateEnum.TAKEN.name());
        craneDemand.setUpdatedAt(new Date());

        return craneDemandRepository.save(craneDemand);
    }

    private void initializeOperatorLocationInCache(User user, AssignCraneDemandDto assignCraneDemandDto) {
        try {
            if (!operatorService.isOperatorLocationCached(user.getId())) {
                log.info("Inicializando localización en cache para operador: {}", user.getId());

                OperatorLocationRequestDto initialLocation = OperatorLocationRequestDto.builder()
                        .latitude(assignCraneDemandDto.getLatitude())
                        .longitude(assignCraneDemandDto.getLongitude())
                        .status("ASSIGNED") // Estado inicial cuando se asigna a una demanda
                        .build();

                operatorService.saveOperatorLocation(user.getId(), initialLocation);
                log.info("Localización inicial cargada en cache para operador: {}", user.getId());
            } else {
                log.info("Operador {} ya tiene localización en cache", user.getId());
            }
        } catch (Exception e) {
            log.warn("Error al inicializar localización en cache para operador {}: {}", user.getId(), e.getMessage());
        }
    }

    public void cancelCraneDemand(String craneDemandId) {
        var craneDemandEntity = craneDemandRepository.findById(craneDemandId);
        if (craneDemandEntity.isEmpty()) {
            throw new ServiceException("Crane demand not found", 404);
        }

        var demand = craneDemandEntity.get();
        demand.setState(CraneDemandStateEnum.CANCELLED.name());
        demand.setUpdatedAt(new Date());
        craneDemandRepository.save(demand);
    }
}

