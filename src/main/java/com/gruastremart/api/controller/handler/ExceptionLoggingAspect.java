package com.gruastremart.api.controller.handler;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import com.gruastremart.api.exception.ServiceException;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ExceptionLoggingAspect {
    @AfterThrowing(pointcut = "execution(* com.taskmanager.backoffice.controller.*.*(..))", throwing = "ex")
    public void logException(Exception ex) {
        if (ex instanceof ServiceException serviceException) {
            if (serviceException.getCode() == 400 && serviceException.getMessage().contains("greater than 0")) {
                log.error("Caught ServiceException with code 400 and negative ID: {}", serviceException.getMessage());
            }
        }
    }}
