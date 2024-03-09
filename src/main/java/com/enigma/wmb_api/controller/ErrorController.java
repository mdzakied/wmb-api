package com.enigma.wmb_api.controller;

import com.enigma.wmb_api.dto.response.common.CommonResponse;
import com.enigma.wmb_api.dto.response.common.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.Arrays;

@RestControllerAdvice
public class ErrorController {
    @ExceptionHandler({ResponseStatusException.class})
    public ResponseEntity<ErrorResponse> responseStatusExceptionHandler (ResponseStatusException e) {

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(String.valueOf(LocalDateTime.now()))
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<ErrorResponse> constraintViolationExceptionHandler(ConstraintViolationException e){
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(String.valueOf(LocalDateTime.now()))
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler({DataIntegrityViolationException.class})
    public ResponseEntity<ErrorResponse> dataIntegrityViolationException (DataIntegrityViolationException e){
        ErrorResponse.ErrorResponseBuilder builder = ErrorResponse.builder();

        HttpStatus httpStatus;

        if (e.getMessage().contains("foregin key constraint")){
            builder.statusCode((HttpStatus.BAD_REQUEST.value()));
            builder.message("can't update or deleted data (reference data)");
            httpStatus= HttpStatus.BAD_REQUEST;

        } else if (e.getMessage().contains("unique constraint") || e.getMessage().contains("Duplicate entry")) {
            builder.statusCode((HttpStatus.CONFLICT.value()));
            builder.message("Data already exist");
            httpStatus= HttpStatus.CONFLICT;
        } else {
            builder.statusCode((HttpStatus.INTERNAL_SERVER_ERROR.value()));
            builder.message("Internal Server Error");
            httpStatus= HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return ResponseEntity.status(httpStatus)
                .body(builder.build());
    }
}
