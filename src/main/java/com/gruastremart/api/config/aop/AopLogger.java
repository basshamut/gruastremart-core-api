package com.gruastremart.api.config.aop;

import com.gruastremart.api.dto.CraneDemandAssignRequestDto;
import com.gruastremart.api.dto.CraneDemandCreateRequestDto;
import com.gruastremart.api.dto.RequestMetadataDto;
import com.gruastremart.api.utils.tools.RequestMetadataExtractorUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
public class AopLogger {
    @Pointcut("execution(* com.gruastremart.api.controller.CraneDemandController.createCraneDemand(..))")
    public void createCraneDemandPointcut() {
    }

    @Pointcut("execution(* com.gruastremart.api.controller.CraneDemandController.updateCraneDemand(..))")
    public void updateCraneDemandPointcut() {
    }

    @Pointcut("execution(* com.gruastremart.api.controller.CraneDemandController.broadcastOperatorLocation(..))")
    public void broadcastOperatorLocationPointcut() {}

    private void logAuditInfo(RequestMetadataDto meta, String currentLocation, String destinationLocation) {
        log.info("AUDITORÍA - Fecha: {}, Usuario: {}, Rol: {}, Email: {}, IP: {}, User-Agent: {}, Ubicación actual: {}, Destino: {}",
                meta.getTimestamp(), meta.getUserId(), meta.getRole(), meta.getEmail(),
                meta.getIp(), meta.getUserAgent(),
                currentLocation, destinationLocation);
    }

    @Before("createCraneDemandPointcut()")
    public void logCreateCraneDemandAuditInfo(org.aspectj.lang.JoinPoint joinPoint) {
        HttpServletRequest request = (HttpServletRequest) joinPoint.getArgs()[1];
        RequestMetadataDto meta = RequestMetadataExtractorUtil.extract(request);
        CraneDemandCreateRequestDto craneDemandRequest = (CraneDemandCreateRequestDto) joinPoint.getArgs()[0];
        logAuditInfo(meta, craneDemandRequest.getCurrentLocation().getName(), craneDemandRequest.getDestinationLocation().getName());
    }

    @Before("updateCraneDemandPointcut()")
    public void logUpdateCraneDemandAuditInfo(org.aspectj.lang.JoinPoint joinPoint) {
        HttpServletRequest request = (HttpServletRequest) joinPoint.getArgs()[1];
        RequestMetadataDto meta = RequestMetadataExtractorUtil.extract(request);
        CraneDemandAssignRequestDto craneDemandRequest = (CraneDemandAssignRequestDto) joinPoint.getArgs()[1];
        logAuditInfo(meta, "N/A", "N/A");
    }

    @Before("broadcastOperatorLocationPointcut()")
    public void logBroadcastOperatorLocation(org.aspectj.lang.JoinPoint joinPoint) {
        String locationJson = null;
        if (joinPoint.getArgs().length > 0) {
            locationJson = String.valueOf(joinPoint.getArgs()[0]);
        }
        String lat = null;
        String lng = null;
        // Intentar extraer lat/lng del JSON si es posible
        if (locationJson != null && locationJson.contains("lat") && locationJson.contains("lng")) {
            try {
                com.fasterxml.jackson.databind.JsonNode node = new com.fasterxml.jackson.databind.ObjectMapper().readTree(locationJson);
                if (node.has("lat")) lat = node.get("lat").asText();
                if (node.has("lng")) lng = node.get("lng").asText();
            } catch (Exception e) {
                log.warn("No se pudo parsear locationJson: {}", locationJson);
            }
        }
        log.info("AUDITORÍA - broadcastOperatorLocation: lat={}, lng={}", lat, lng);
    }
}
