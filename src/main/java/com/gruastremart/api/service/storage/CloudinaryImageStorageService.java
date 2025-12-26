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
        try {
            // Generar nombre único para el archivo
            String publicId = "payments/payment_" + context + "_" + UUID.randomUUID();

            // Subir a Cloudinary
            Map uploadResult = cloudinary.uploader().upload(
                    file.getInputStream(),
                    ObjectUtils.asMap(
                            "public_id", publicId,
                            "resource_type", "auto",
                            "folder", "gruastremart/payments"
                    )
            );

            String imageUrl = (String) uploadResult.get("secure_url");
            log.info("Imagen de pago guardada en Cloudinary: {}", imageUrl);

            return imageUrl;

        } catch (IOException e) {
            log.error("Error al guardar imagen de pago en Cloudinary: {}", e.getMessage());
            throw new ServiceException("Error al guardar la imagen del comprobante", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Override
    public String getStorageType() {
        return "CLOUDINARY";
    }
}
