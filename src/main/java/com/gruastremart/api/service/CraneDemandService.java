package com.gruastremart.api.service;

import com.gruastremart.api.dto.CraneDemandCreateRequestDto;
import com.gruastremart.api.dto.CraneDemandResponseDto;
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
    private final OperatorRepository operatorRepository;

    private final EmailService emailService;

    private final SimpMessagingTemplate messagingTemplate;

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

        notifyNewDemand(response);

        return response;
    }

    private void validateUserHasNoActiveDemand(String userId) {
        var demands = craneDemandRepository.findByCreatedByUserId(userId);
        if (demands.stream().anyMatch(CraneDemand::isActiveOrTaken)) {
            throw new ServiceException("User already has an active or taken crane demand", 400);
        }
    }

    private void notifyNewDemand(CraneDemandResponseDto dto) {
        messagingTemplate.convertAndSend("/topic/new-demand", dto);
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
        emailService.sendResponseOfCraneDemandEmail(user.getName(), user.getEmail());
        messagingTemplate.convertAndSend("/topic/demand-taken/" + craneDemand.getId(), CraneDemandMapper.MAPPER.mapToDto(updated));
        return updated;
    }

    public void cancelCraneDemand(String craneDemandId) {
        var craneDemandEntity = craneDemandRepository.findById(craneDemandId);
        if (craneDemandEntity.isEmpty()) {
            throw new ServiceException("User not found", 404);
        }
        craneDemandEntity.get().setState(CraneDemandStateEnum.CANCELLED.name());
        craneDemandRepository.save(craneDemandEntity.get());
    }

    public void notifyOperatorLocation(String craneDemandId, String locationJson) {
        messagingTemplate.convertAndSend("/topic/operator-location/" + craneDemandId, locationJson);
    }

    public void broadcastOperatorLocation(String locationJson) {
        messagingTemplate.convertAndSend("/topic/operator-location/broadcast", locationJson);
    }
}
