package com.gruastremart.api.service;

import com.gruastremart.api.dto.PaymentCreateRequestDto;
import com.gruastremart.api.dto.PaymentResponseDto;
import com.gruastremart.api.dto.PaymentVerifyRequestDto;
import com.gruastremart.api.exception.ServiceException;
import com.gruastremart.api.mapper.PaymentMapper;
import com.gruastremart.api.persistance.entity.CraneDemand;
import com.gruastremart.api.persistance.entity.Payment;
import com.gruastremart.api.persistance.repository.CraneDemandRepository;
import com.gruastremart.api.persistance.repository.PaymentRepository;
import com.gruastremart.api.utils.enums.CraneDemandStateEnum;
import com.gruastremart.api.utils.enums.PaymentStatusEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final CraneDemandRepository craneDemandRepository;
    private final PaymentMapper paymentMapper;
    private final EmailService emailService;

    // Directorio donde se almacenarán las imágenes (configurable)
    private static final String UPLOAD_DIR = "uploads/payments/";

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
        String imageUrl = savePaymentImage(dto.getPaymentImage(), dto.getDemandId());

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
     * Obtiene los detalles de un pago específico
     */
    public PaymentResponseDto getPaymentById(String paymentId) {
        log.info("Obteniendo detalles del pago: {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ServiceException("Pago no encontrado", HttpStatus.NOT_FOUND.value()));

        return paymentMapper.toResponseDto(payment);
    }

    /**
     * Verifica un pago (solo administradores)
     */
    public PaymentResponseDto verifyPayment(String paymentId, PaymentVerifyRequestDto dto, String verifiedByUserId) {
        log.info("Verificando pago: {} por usuario: {}", paymentId, verifiedByUserId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ServiceException("Pago no encontrado", HttpStatus.NOT_FOUND.value()));

        // Validar que el estado sea válido
        if (!dto.getStatus().equals(PaymentStatusEnum.VERIFIED.name()) && 
            !dto.getStatus().equals(PaymentStatusEnum.REJECTED.name())) {
            throw new ServiceException("Estado inválido. Debe ser VERIFIED o REJECTED", HttpStatus.BAD_REQUEST.value());
        }

        // Actualizar el pago
        payment.setStatus(dto.getStatus());
        payment.setVerifiedByUserId(verifiedByUserId);
        payment.setVerificationComments(dto.getVerificationComments());
        payment.setVerifiedAt(new Date());
        payment.setUpdatedAt(new Date());

        Payment updatedPayment = paymentRepository.save(payment);

        // Enviar notificación por email al usuario
        sendPaymentVerificationEmail(payment, dto.getStatus());

        log.info("Pago {} actualizado a estado: {}", paymentId, dto.getStatus());

        return paymentMapper.toResponseDto(updatedPayment);
    }

    /**
     * Rechaza un pago (solo administradores)
     */
    public PaymentResponseDto rejectPayment(String paymentId, PaymentVerifyRequestDto dto, String verifiedByUserId) {
        dto.setStatus(PaymentStatusEnum.REJECTED.name());
        return verifyPayment(paymentId, dto, verifiedByUserId);
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
     * Guarda la imagen del comprobante de pago
     */
    private String savePaymentImage(MultipartFile file, String demandId) {
        try {
            // Crear directorio si no existe
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generar nombre único para el archivo
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null ? 
                    originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
            String uniqueFilename = "payment_" + demandId + "_" + UUID.randomUUID() + fileExtension;

            // Guardar el archivo
            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            log.info("Imagen de pago guardada: {}", filePath);

            // Retornar la URL relativa (o absoluta según configuración)
            return UPLOAD_DIR + uniqueFilename;

        } catch (IOException e) {
            log.error("Error al guardar imagen de pago: {}", e.getMessage());
            throw new ServiceException("Error al guardar la imagen del comprobante", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
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
