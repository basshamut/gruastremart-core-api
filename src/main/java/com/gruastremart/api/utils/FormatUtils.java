package com.gruastremart.api.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import com.gruastremart.api.controller.handler.json.HttpErrorInfoJson;
import org.springframework.http.HttpStatus;

@Slf4j
public class FormatUtils {

    private FormatUtils(){}

    public static HttpErrorInfoJson httpErrorInfoFormatted(HttpStatus status, HttpServletRequest request, Exception ex){
        final String path = request.getRequestURI();
        final String message = ex.getMessage();
        log.debug("Returning HttpStatus: {} for path: {} , message: {} ", status, path, message);
        return new HttpErrorInfoJson(status, path, message);
    }

}
