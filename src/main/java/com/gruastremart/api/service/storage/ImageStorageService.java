package com.gruastremart.api.service.storage;

import org.springframework.web.multipart.MultipartFile;

/**
 * Interfaz para abstraer el almacenamiento de imágenes
 * Permite implementar diferentes estrategias: local, Cloudinary, S3, etc.
 */
public interface ImageStorageService {
    
    /**
     * Guarda una imagen y retorna su URL
     * 
     * @param file Archivo de imagen a guardar
     * @param context Contexto/identificador para la imagen (ej: demandId para pagos)
     * @return URL de la imagen guardada
     */
    String saveImage(MultipartFile file, String context);
    
    /**
     * Obtiene el tipo de almacenamiento configurado
     * 
     * @return Tipo de almacenamiento (LOCAL, CLOUDINARY, S3, etc.)
     */
    String getStorageType();
}
