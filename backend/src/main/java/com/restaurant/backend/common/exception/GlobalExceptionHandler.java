package com.restaurant.backend.common.exception;

import com.restaurant.backend.common.response.ApiResponse;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        ErrorResponse errorResponse = ErrorResponse.of(errorCode, exception.getMessage());

        return ResponseEntity.status(errorCode.getStatus()).body(ApiResponse.failure(errorResponse));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException exception) {
        Map<String, String> details = new LinkedHashMap<>();

        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            details.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return ResponseEntity.badRequest()
                .body(ApiResponse.failure(ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, details)));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception exception) {
        return ResponseEntity.internalServerError()
                .body(ApiResponse.failure(ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR, exception.getMessage())));
    }
}
