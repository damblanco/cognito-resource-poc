package com.damian.cognitoresourcepoc.controller.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Exception handler for {@link HttpStatusCodeException} .
     */
    @ExceptionHandler(value = {HttpStatusCodeException.class})
    protected ResponseEntity<Object> handleHttpStatusCodeException(HttpStatusCodeException ex, WebRequest request) {
        return handleExceptionInternal(ex, null, new HttpHeaders(), ex.getStatusCode(), request);
    }

    /**
     * Exception handler for {@link RuntimeException} .
     */
    @ExceptionHandler(value = {RuntimeException.class})
    protected ResponseEntity<Object> handleRuntimeException(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex, null, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
