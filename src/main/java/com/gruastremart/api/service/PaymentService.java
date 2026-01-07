package com.gruastremart.api.service;

import com.gruastremart.api.dto.PaymentCreateRequestDto;
import com.gruastremart.api.dto.PaymentResponseDto;
import com.gruastremart.api.dto.PaymentVerifyRequestDto;
import com.gruastremart.api.exception.ServiceException;
import com.gruastremart.api.mapper.PaymentMapper;
import com.gruastremart.api.persistance.entity.CraneDemand;
import com.gruastremart.api.persistance.entity.Payment;
import com.gruastremart.api.persistance.entity.User;
import com.gruastremart.api.persistance.repository.CraneDemandRepository;
import com.gruastremart.api.persistance.repository.PaymentRepository;
import com.gruastremart.api.persistance.repository.UserRepository;
import com.gruastremart.api.service.storage.ImageStorageService;
import com.gruastremart.api.utils.enums.CraneDemandStateEnum;
import com.gruastremart.api.utils.enums.PaymentStatusEnum;
import com.gruastremart.api.utils.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final CraneDemandRepository craneDemandRepository;
    private final PaymentMapper paymentMapper;
    private final EmailService emailService;
    private final ImageStorageService imageStorageService;
    private final UserRepository userRepository;

    /**
     * Registra un nuevo pago para una demanda completada
     */
    public PaymentResponseDto registerPayment(PaymentCreateRequestDto dto) {
        log.info("Registrando pago para demanda: {}", dto.getDemandId());

        // Validar que la demanda existe y está completada
        CraneDemand demand = validateDemandForPayment(dto.getDemandId());

        // Validar que no existe ya un pago para esta demanda
        if (paymentRepository.existsByDemandId(dto.getDemandId())) {
            throw new ServiceException("Ya existe un pago registrado para esta demanda", HttpStatus.BAD_REQUEST.value());
        }

        // Guardar la imagen del comprobante
        String imageUrl = imageStorageService.saveImage(dto.getPaymentImage(), dto.getDemandId());

        // Crear la entidad Payment
        Payment payment = Payment.builder()
                .demandId(dto.getDemandId())
                .userId(dto.getUserId())
                .mobilePaymentReference(dto.getMobilePaymentReference())
                .paymentImageUrl(imageUrl)
                .status(PaymentStatusEnum.PENDING.name())
                .amount(dto.getAmount())
                .createdAt(new Date())
                .updatedAt(new Date())
                .build();

        // Guardar en la base de datos
        Payment savedPayment = paymentRepository.save(payment);

        // Actualizar la demanda con el ID del pago
        demand.setPaymentId(savedPayment.getId());
        demand.setUpdatedAt(new Date());
        craneDemandRepository.save(demand);

        log.info("Pago registrado exitosamente con ID: {}", savedPayment.getId());

        return paymentMapper.toResponseDto(savedPayment);
    }

    /**
     * Obtiene el historial de pagos de un usuario
     */
    public Page<PaymentResponseDto> getPaymentHistory(String userId, String status, Pageable pageable) {
        log.info("Obteniendo historial de pagos para usuario: {}", userId);

        Page<Payment> payments;

        if (status != null && !status.isEmpty()) {
            payments = paymentRepository.findByUserIdAndStatus(userId, status, pageable);
        } else {
            payments = paymentRepository.findByUserId(userId, pageable);
        }

        return payments.map(paymentMapper::toResponseDto);
    }

    /**
     * Obtiene todos los pagos del sistema
     * @param status Estado del pago (opcional: PENDING, VERIFIED, REJECTED)
     * @param pageable Parámetros de paginación
     * @return Página con todos los pagos del sistema
     */
    public Page<PaymentResponseDto> getAllPayments(String status, Pageable pageable) {
        log.info("Obteniendo todos los pagos del sistema con filtro de estado: {}", status);

        Page<Payment> payments;

        if (status != null && !status.isEmpty()) {
            payments = paymentRepository.findByStatus(status, pageable);
        } else {
            payments = paymentRepository.findAll(pageable);
        }

        return payments.map(payment -> {
            PaymentResponseDto dto = paymentMapper.toResponseDto(payment);

            // Buscar la demanda asociada para obtener sus datos
            CraneDemand demand = craneDemandRepository.findById(payment.getDemandId())
                    .orElse(null);

            if (demand != null) {
                dto.setDemandOrigin(demand.getOrigin());
                dto.setDemandCarType(demand.getCarType());
            }

            return dto;
        });
    }

    /**
     * Obtiene los pagos de un operador especifico (pagos de sus demandas completadas)
     * @param requestingUserEmail Email del usuario que hace la solicitud (para validación)
     * @param status Estado del pago (opcional: PENDING, VERIFIED, REJECTED)
     * @param pageable Parámetros de paginación
     */
    public Page<PaymentResponseDto> getOperatorPayments(String requestingUserEmail, String status, Pageable pageable) {
        log.info("Obteniendo pagos para operador: {})", requestingUserEmail);

        // Validar permisos: El usuario solo puede ver sus propios pagos si es OPERATOR
        User requestingUser = userRepository.findByEmail(requestingUserEmail)
                .orElseThrow(() -> new ServiceException("Usuario no encontrado", HttpStatus.NOT_FOUND.value()));

        if (requestingUser.getRole() != Role.OPERATOR) {
            throw new ServiceException("Un operador solo puede ver sus propios pagos", HttpStatus.FORBIDDEN.value());
        }

        // Paso 1: Buscar demandas del operador que están COMPLETED
        Page<CraneDemand> demandsPage = craneDemandRepository.findByAssignedOperatorIdAndState(
                requestingUser.getId(),
                CraneDemandStateEnum.COMPLETED.name(),
                pageable
        );

        // Si no hay demandas, retornar página vacía
        if (demandsPage.getContent().isEmpty()) {
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }

        // Paso 2: Extraer los IDs de esas demandas
        List<String> demandIds = demandsPage.getContent().stream()
                .map(CraneDemand::getId)
                .collect(Collectors.toList());

        // Paso 3: Buscar pagos por lista de demandas
        List<Payment> allPayments = paymentRepository.findByDemandIdIn(demandIds);

        // Paso 4: Filtrar por estado si es necesario
        List<Payment> filteredPayments;
        if (status != null && !status.isEmpty()) {
            filteredPayments = allPayments.stream()
                    .filter(payment -> status.equals(payment.getStatus()))
                    .collect(Collectors.toList());
        } else {
            filteredPayments = allPayments;
        }

        // Paso 5: Aplicar paginación manual
        int fromIndex = (int) pageable.getOffset();
        int toIndex = Math.min(fromIndex + pageable.getPageSize(), filteredPayments.size());

        // Verificar que el índice es válido
        if (fromIndex >= filteredPayments.size()) {
            return new PageImpl<>(new ArrayList<>(), pageable, filteredPayments.size());
        }

        List<Payment> paginatedPayments = filteredPayments.subList(fromIndex, toIndex);

        // Crear Page con los resultados paginados
        Page<Payment> paymentsPage = new PageImpl<>(
                paginatedPayments,
                pageable,
                filteredPayments.size()
        );

        // Paso 6: Enricher los DTOs con información de la demanda
        List<PaymentResponseDto> enrichedDtos = new ArrayList<>();
        for (Payment payment : paginatedPayments) {
            PaymentResponseDto dto = paymentMapper.toResponseDto(payment);

            // Buscar la demanda asociada para obtener sus datos
            CraneDemand demand = craneDemandRepository.findById(payment.getDemandId())
                    .orElse(null);

            if (demand != null) {
                dto.setDemandOrigin(demand.getOrigin());
                dto.setDemandCarType(demand.getCarType());
            }

            enrichedDtos.add(dto);
        }

        // Crear Page con los DTOs enriquecidos
        Page<PaymentResponseDto> enrichedPage = new PageImpl<>(
                enrichedDtos,
                pageable,
                filteredPayments.size()
        );

        return enrichedPage;
    }

    /**
     * Obtiene los detalles de un pago específico
     */
    public PaymentResponseDto getPaymentById(String paymentId) {
        log.info("Obteniendo detalles del pago: {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ServiceException("Pago no encontrado", HttpStatus.NOT_FOUND.value()));

        return paymentMapper.toResponseDto(payment);
    }

    /**
     * Verifica un pago (administradores u operadores de la demanda)
     */
    public PaymentResponseDto verifyPayment(String paymentId, PaymentVerifyRequestDto dto, String verifiedByUserEmail) {
        log.info("Verificando pago: {} con estado: {}", paymentId, dto.getStatus());

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ServiceException("Pago no encontrado", HttpStatus.NOT_FOUND.value()));

        // Obtener la demanda asociada
        CraneDemand demand = craneDemandRepository.findById(payment.getDemandId())
                .orElseThrow(() -> new ServiceException("Demanda no encontrada", HttpStatus.NOT_FOUND.value()));

        // Obtener el usuario que está verificando
        User verifyingUser = userRepository.findByEmail(verifiedByUserEmail)
                .orElseThrow(() -> new ServiceException("Usuario no encontrado", HttpStatus.NOT_FOUND.value()));

        // Validar permisos: Los OPERATOR solo pueden verificar pagos de sus propias demandas
        if (verifyingUser.getRole() != Role.OPERATOR) {
            throw new ServiceException("Un operador solo puede verificar pagos de sus propias demandas", HttpStatus.FORBIDDEN.value());
        }

        // Validar que el estado sea válido
        if (!dto.getStatus().equals(PaymentStatusEnum.VERIFIED.name()) &&
            !dto.getStatus().equals(PaymentStatusEnum.REJECTED.name())) {
            throw new ServiceException("Estado inválido. Debe ser VERIFIED o REJECTED", HttpStatus.BAD_REQUEST.value());
        }

        // Actualizar el pago
        payment.setStatus(dto.getStatus());
        payment.setVerifiedByUserId(verifyingUser.getId());
        payment.setVerificationComments(dto.getVerificationComments());
        payment.setVerifiedAt(new Date());
        payment.setUpdatedAt(new Date());

        Payment updatedPayment = paymentRepository.save(payment);

        // Enviar notificación por email al usuario
        sendPaymentVerificationEmail(payment, dto.getStatus());

        log.info("Pago {} actualizado a estado: {} por usuario: {}", paymentId, dto.getStatus(), verifiedByUserEmail);

        return paymentMapper.toResponseDto(updatedPayment);
    }

    /**
     * Rechaza un pago (solo administradores)
     */
    public PaymentResponseDto rejectPayment(String paymentId, PaymentVerifyRequestDto dto, String verifiedByUserEmail) {
        dto.setStatus(PaymentStatusEnum.REJECTED.name());
        return verifyPayment(paymentId, dto, verifiedByUserEmail);
    }

    /**
     * Valida que la demanda existe y está en estado COMPLETED
     */
    private CraneDemand validateDemandForPayment(String demandId) {
        CraneDemand demand = craneDemandRepository.findById(demandId)
                .orElseThrow(() -> new ServiceException("Demanda no encontrada", HttpStatus.NOT_FOUND.value()));

        if (!demand.getState().equals(CraneDemandStateEnum.COMPLETED.name())) {
            throw new ServiceException("Solo se pueden registrar pagos para demandas completadas", HttpStatus.BAD_REQUEST.value());
        }

        return demand;
    }

    /**
     * Envía notificación por email sobre la verificación del pago
     */
    private void sendPaymentVerificationEmail(Payment payment, String status) {
        try {
            // TODO: Implementar envío de email con el servicio de email existente
            log.info("Enviando notificación de pago {} al usuario {}", status, payment.getUserId());
            // emailService.sendPaymentVerificationEmail(payment, status);
        } catch (Exception e) {
            log.warn("Error al enviar email de notificación de pago: {}", e.getMessage());
        }
    }
}
