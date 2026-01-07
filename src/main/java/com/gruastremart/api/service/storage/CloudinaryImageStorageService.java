package com.gruastremart.api.service.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.gruastremart.api.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/**
 * Implementación de almacenamiento en Cloudinary.
 * Usado en producción y otros ambientes cloud.
 */
@Service
@Slf4j
@ConditionalOnProperty(
        name = "app.image.storage",
        havingValue = "cloudinary"
)
@RequiredArgsConstructor
public class CloudinaryImageStorageService implements ImageStorageService {

    private final Cloudinary cloudinary;

    @Override
    public String saveImage(MultipartFile file, String context) {
        // Validar el archivo antes de procesarlo
        if (file == null || file.isEmpty()) {
            log.error("El archivo de imagen es nulo o está vacío");
            throw new ServiceException("El archivo de imagen es requerido y no puede estar vacío", HttpStatus.BAD_REQUEST.value());
        }

        log.info("Intentando guardar imagen - Nombre: {}, Tamaño: {} bytes, Tipo: {}, Context: {}", 
                file.getOriginalFilename(), file.getSize(), file.getContentType(), context);

        try {
            // Validar el tamaño del archivo (máximo 10MB)
            long maxSize = 10 * 1024 * 1024; // 10MB
            if (file.getSize() > maxSize) {
                log.error("El archivo excede el tamaño máximo permitido: {} bytes (máximo: {} bytes)", 
                        file.getSize(), maxSize);
                throw new ServiceException("El archivo es demasiado grande. Tamaño máximo permitido: 10MB", 
                        HttpStatus.BAD_REQUEST.value());
            }

            // Generar nombre único para el archivo
            String publicId = "payments/payment_" + context + "_" + UUID.randomUUID();
            log.debug("Public ID generado: {}", publicId);

            // Convertir MultipartFile a byte array para Cloudinary
            // Cloudinary no acepta InputStream directamente en algunas configuraciones
            byte[] fileBytes = file.getBytes();
            log.debug("Archivo convertido a byte array: {} bytes", fileBytes.length);

            // Subir a Cloudinary usando byte array
            Map uploadResult = cloudinary.uploader().upload(
                    fileBytes,
                    ObjectUtils.asMap(
                            "public_id", publicId,
                            "resource_type", "auto",
                            "folder", "gruastremart/payments"
                    )
            );

            String imageUrl = (String) uploadResult.get("secure_url");
            log.info("Imagen de pago guardada exitosamente en Cloudinary: {}", imageUrl);

            return imageUrl;

        } catch (IOException e) {
            log.error("Error de IOException al guardar imagen de pago en Cloudinary", e);
            log.error("Detalles del error - Mensaje: {}, Causa: {}", 
                    e.getMessage(), 
                    e.getCause() != null ? e.getCause().getMessage() : "No hay causa");
            throw new ServiceException(
                    "Error al guardar la imagen del comprobante: " + e.getMessage(), 
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        } catch (Exception e) {
            log.error("Error inesperado al guardar imagen de pago en Cloudinary", e);
            throw new ServiceException(
                    "Error inesperado al guardar la imagen del comprobante: " + e.getMessage(), 
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Override
    public String getStorageType() {
        return "CLOUDINARY";
    }
}
