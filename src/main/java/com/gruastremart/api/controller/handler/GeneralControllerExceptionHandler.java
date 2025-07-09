package com.gruastremart.api.controller.handler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import com.gruastremart.api.dto.HttpErrorInfoDto;
import com.gruastremart.api.exception.ServiceException;
import com.gruastremart.api.utils.tools.FormatUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;

@RestControllerAdvice
@Slf4j
public class GeneralControllerExceptionHandler {

    @ExceptionHandler(value = {Exception.class})
    @ResponseBody
    public ResponseEntity<HttpErrorInfoDto> handleException(Exception exception, HttpServletRequest request) {
        HttpErrorInfoDto httpErrorInfoDto = FormatUtils.httpErrorInfoFormatted(HttpStatus.INTERNAL_SERVER_ERROR, request, exception);
        log.error(httpErrorInfoDto.toString());
        log.error(Arrays.toString(exception.getStackTrace()));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(httpErrorInfoDto);
    }

    @ExceptionHandler(value = {ServiceException.class})
    @ResponseBody
    public ResponseEntity<HttpErrorInfoDto> handleServiceException(ServiceException serviceException, HttpServletRequest request) {
        HttpErrorInfoDto httpErrorInfoDto;

        switch (serviceException.getCode()) {
            case 400 -> {
                httpErrorInfoDto = FormatUtils.httpErrorInfoFormatted(HttpStatus.BAD_REQUEST, request, serviceException);
                log.error(httpErrorInfoDto.toString());
                log.error(Arrays.toString(serviceException.getStackTrace()));
                return new ResponseEntity<>(httpErrorInfoDto, HttpStatus.BAD_REQUEST);
            }
            case 404 -> {
                httpErrorInfoDto = FormatUtils.httpErrorInfoFormatted(HttpStatus.NOT_FOUND, request, serviceException);
                log.error(httpErrorInfoDto.toString());
                log.error(Arrays.toString(serviceException.getStackTrace()));
                return new ResponseEntity<>(httpErrorInfoDto, HttpStatus.NOT_FOUND);
            }
            default -> {
                httpErrorInfoDto = FormatUtils.httpErrorInfoFormatted(HttpStatus.INTERNAL_SERVER_ERROR, request, serviceException);
                log.error(httpErrorInfoDto.toString());
                log.error(Arrays.toString(serviceException.getStackTrace()));
                return new ResponseEntity<>(httpErrorInfoDto, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

}
