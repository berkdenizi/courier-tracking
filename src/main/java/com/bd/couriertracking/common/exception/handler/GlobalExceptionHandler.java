package com.bd.couriertracking.common.exception.handler;

import com.bd.couriertracking.common.api.ApiResult;
import com.bd.couriertracking.common.api.ErrorCode;
import com.bd.couriertracking.common.api.ErrorInfo;
import com.bd.couriertracking.common.exception.DuplicateResourceException;
import com.bd.couriertracking.common.exception.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResult<Void>> handleValidation(MethodArgumentNotValidException ex) {
        String field = ex.getBindingResult().getAllErrors().stream()
                .findFirst()
                .map(e -> e instanceof FieldError fe ? fe.getField() : e.getObjectName())
                .orElse("request");
        String msg = ex.getBindingResult().getAllErrors().stream()
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse("Validation failed");

        var err = ErrorInfo.builder()
                .code(ErrorCode.VALIDATION_ERROR)
                .message(msg)
                .field(field)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResult.error(err));
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResult<Void>> handleDuplicate(DuplicateResourceException ex) {
        var err = ErrorInfo.builder()
                .code(ErrorCode.DUPLICATE_RESOURCE)
                .message(ex.getMessage())
                .field(ex.getField())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResult.error(err));
    }

    @ExceptionHandler({ResourceNotFoundException.class, EntityNotFoundException.class})
    public ResponseEntity<ApiResult<Void>> handleNotFound(RuntimeException ex) {
        var err = ErrorInfo.builder()
                .code(ErrorCode.NOT_FOUND)
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResult.error(err));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResult<Void>> handleDataIntegrity(DataIntegrityViolationException ex) {
        var err = ErrorInfo.builder()
                .code(ErrorCode.DATA_INTEGRITY_VIOLATION)
                .message("Data integrity violation")
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResult.error(err));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResult<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        var err = ErrorInfo.builder()
                .code(ErrorCode.BUSINESS_ERROR)
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResult.error(err));
    }
}