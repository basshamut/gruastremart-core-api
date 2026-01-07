package com.gruastremart.api.service.storage;

import com.gruastremart.api.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Implementación de almacenamiento local de imágenes.
 * Usado en desarrollo y ambientes locales.
 */
@Service
@Slf4j
@ConditionalOnProperty(
        name = "app.image.storage",
        havingValue = "local",
        matchIfMissing = false
)
@RequiredArgsConstructor
public class LocalImageStorageService implements ImageStorageService {

    @Value("${app.image.upload-dir:uploads/}")
    private String uploadDir;

    @Value("${app.image.server-url:http://localhost:8080}")
    private String serverUrl;

    @Value("${server.servlet.context-path:/}")
    private String contextPath;

    @Override
    public String saveImage(MultipartFile file, String context) {
        try {
            // Crear directorio si no existe
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generar nombre único para el archivo
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null ?
                    originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
            String uniqueFilename = "payment_" + context + "_" + UUID.randomUUID() + fileExtension;

            // Guardar el archivo
            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            log.info("Imagen de pago guardada localmente: {}", filePath);
            log.info("Configuración - serverUrl: {}, contextPath: {}, uploadDir: {}, uniqueFilename: {}",
                    serverUrl, contextPath, uploadDir, uniqueFilename);

            // Construir la URL completa
            String fullUrl = serverUrl + contextPath + "/" + uploadDir + uniqueFilename;
            log.info("URL de imagen generada: {}", fullUrl);

            return fullUrl;

        } catch (IOException e) {
            log.error("Error al guardar imagen de pago localmente: {}", e.getMessage());
            throw new ServiceException("Error al guardar la imagen del comprobante", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Override
    public String getStorageType() {
        return "LOCAL";
    }
}
