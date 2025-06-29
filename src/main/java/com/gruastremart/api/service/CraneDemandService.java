package com.gruastremart.api.service;

import com.gruastremart.api.dto.CraneDemandCreateRequestDto;
import com.gruastremart.api.dto.CraneDemandResponseDto;
import com.gruastremart.api.dto.OperatorLocationRequestDto;
import com.gruastremart.api.exception.ServiceException;
import com.gruastremart.api.mapper.CraneDemandMapper;
import com.gruastremart.api.persistance.entity.CraneDemand;
import com.gruastremart.api.persistance.entity.CraneOperator;
import com.gruastremart.api.persistance.entity.User;
import com.gruastremart.api.persistance.repository.CraneDemandRepository;
import com.gruastremart.api.persistance.repository.OperatorRepository;
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
    private final OperatorRepository operatorRepository;
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
        var response = CraneDemandMapper.MAPPER.mapToDto(saved);

        return response;
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

    public Optional<CraneDemandResponseDto> assignCraneDemand(String craneDemandId, String userEmail) {
        var craneDemand = getCreaneDemandById(craneDemandId);
        var userThatCreatedDemand = getUserById(craneDemand.getCreatedByUserId());
        var userThatTakeDemand = getUserByEmail(userEmail);
        var userAsOperator = getOperatorByUserId(userThatTakeDemand.getId());

        CraneDemand updated = updateCraneDemandWithOperatorInformation(craneDemand, userThatCreatedDemand, userAsOperator);

        return Optional.of(CraneDemandMapper.MAPPER.mapToDto(updated));
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

    private CraneOperator getOperatorByUserId(String userId) {
        var operator = operatorRepository.findByUserId(userId);
        if (operator.isEmpty()) {
            throw new ServiceException("Operator not found", 404);
        }

        return operator.get();
    }

    private CraneDemand updateCraneDemandWithOperatorInformation(CraneDemand craneDemand, User user, CraneOperator operator) {
        craneDemand.setEditedByUserId(user.getId());
        craneDemand.setAssignedOperatorId(operator.getId());
        craneDemand.setState(CraneDemandStateEnum.TAKEN.name());
        craneDemand.setUpdatedAt(new Date());
        CraneDemand updated = craneDemandRepository.save(craneDemand);

        // Cargar la información del operador en el cache de localización
        initializeOperatorLocationInCache(operator.getId());

        emailService.sendResponseOfCraneDemandEmail(user.getName(), user.getEmail());
        return updated;
    }

    /**
     * Inicializa la localización del operador en cache con valores por defecto
     * cuando se asigna a una demanda por primera vez
     */
    private void initializeOperatorLocationInCache(String operatorId) {
        try {
            // Verificar si el operador ya tiene localización en cache
            if (!operatorService.isOperatorLocationCached(operatorId)) {
                log.info("Inicializando localización en cache para operador: {}", operatorId);

                // Crear una localización inicial con coordenadas por defecto
                OperatorLocationRequestDto initialLocation = OperatorLocationRequestDto.builder()
                        .latitude(0.0) // Coordenadas por defecto, se actualizarán cuando el operador envíe su ubicación real
                        .longitude(0.0)
                        .status("ASSIGNED") // Estado inicial cuando se asigna a una demanda
                        .build();

                operatorService.saveOperatorLocation(operatorId, initialLocation);
                log.info("Localización inicial cargada en cache para operador: {}", operatorId);
            } else {
                log.info("Operador {} ya tiene localización en cache", operatorId);
            }
        } catch (Exception e) {
            log.warn("Error al inicializar localización en cache para operador {}: {}", operatorId, e.getMessage());
        }
    }

    public void cancelCraneDemand(String craneDemandId) {
        var craneDemandEntity = craneDemandRepository.findById(craneDemandId);
        if (craneDemandEntity.isEmpty()) {
            throw new ServiceException("User not found", 404);
        }
        craneDemandEntity.get().setState(CraneDemandStateEnum.CANCELLED.name());
        craneDemandRepository.save(craneDemandEntity.get());
    }
}
