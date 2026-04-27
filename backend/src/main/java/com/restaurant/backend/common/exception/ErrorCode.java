package com.restaurant.backend.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_500", "Internal server error"),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "COMMON_400", "Invalid input value"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON_404", "Resource not found");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
