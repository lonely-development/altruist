package com.altruist.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
@Slf4j
public class ControllerAdvisor {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ExceptionContainer> handleDataIntegrityViolations(
            DataIntegrityViolationException exception) {
        return ResponseEntity
                .badRequest()
                .body(new ExceptionContainer(exception.getMessage()));
    }

    // TODO: make json format common with javax validation (or build common format for all exception handler).
    @Data
    @AllArgsConstructor
    public static class ExceptionContainer {
        private String message;
        private final LocalDateTime timestamp = LocalDateTime.now();
    }

}