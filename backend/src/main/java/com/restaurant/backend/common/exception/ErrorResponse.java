package com.restaurant.backend.common.exception;

import java.util.Map;

public record ErrorResponse(
        String code,
        String message,
        Map<String, String> details
) {

    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.getCode(), errorCode.getMessage(), Map.of());
    }

    public static ErrorResponse of(ErrorCode errorCode, String message) {
        return new ErrorResponse(errorCode.getCode(), message, Map.of());
    }

    public static ErrorResponse of(ErrorCode errorCode, Map<String, String> details) {
        return new ErrorResponse(errorCode.getCode(), errorCode.getMessage(), details);
    }
}
