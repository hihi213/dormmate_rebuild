package com.dormmate.global.error;

import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, Object> errors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.computeIfAbsent(fieldError.getField(), key -> new java.util.ArrayList<String>());
            @SuppressWarnings("unchecked")
            java.util.List<String> messages = (java.util.List<String>) errors.get(fieldError.getField());
            messages.add(fieldError.getDefaultMessage());
        }
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, "입력값이 유효하지 않습니다.");
        problem.setTitle(HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase());
        problem.setProperty("code", "VALIDATION_FAILED");
        problem.setProperty("errors", errors);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(problem);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, Object> errors = new HashMap<>();
        ex.getConstraintViolations().forEach((violation) -> {
            String path = violation.getPropertyPath() != null ? violation.getPropertyPath().toString() : "unknown";
            errors.computeIfAbsent(path, key -> new java.util.ArrayList<String>());
            @SuppressWarnings("unchecked")
            java.util.List<String> messages = (java.util.List<String>) errors.get(path);
            messages.add(violation.getMessage());
        });
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, "입력값이 유효하지 않습니다.");
        problem.setTitle(HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase());
        problem.setProperty("code", "VALIDATION_FAILED");
        problem.setProperty("errors", errors);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(problem);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "요청 본문을 읽을 수 없습니다.");
        problem.setTitle(HttpStatus.BAD_REQUEST.getReasonPhrase());
        problem.setProperty("code", "BAD_REQUEST");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ProblemDetail> handleResponseStatus(ResponseStatusException ex) {
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
        HttpStatus resolved = status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR;
        String detail = ex.getReason() != null ? ex.getReason() : resolved.getReasonPhrase();
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(resolved, detail);
        problem.setTitle(resolved.getReasonPhrase());
        problem.setProperty("code", resolved.name());
        return ResponseEntity.status(resolved).body(problem);
    }

    @ExceptionHandler(ErrorResponseException.class)
    public ResponseEntity<ProblemDetail> handleErrorResponse(ErrorResponseException ex) {
        ProblemDetail problem = ex.getBody();
        if (problem.getTitle() == null || problem.getTitle().isBlank()) {
            problem.setTitle(ex.getStatusCode().toString());
        }
        if (problem.getProperties() == null || !problem.getProperties().containsKey("code")) {
            problem.setProperty("code", ex.getStatusCode().toString());
        }
        return ResponseEntity.status(ex.getStatusCode()).body(problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnhandled(Exception ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "서버에 오류가 발생했습니다.");
        problem.setTitle(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        problem.setProperty("code", "SERVER_ERROR");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
    }
}
