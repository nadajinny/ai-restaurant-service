package com.restaurant.backend.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.restaurant.backend.common.exception.ErrorCode;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        String message,
        T data,
        String errorCode,
        Map<String, String> details
) {

    public static <T> ApiResponse<T> success(T data) {
        return success("요청이 성공했습니다.", data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, null, null);
    }

    public static <T> ApiResponse<T> failure(ErrorCode errorCode) {
        return new ApiResponse<>(false, errorCode.getMessage(), null, errorCode.getCode(), null);
    }

    public static <T> ApiResponse<T> failure(ErrorCode errorCode, String message) {
        return new ApiResponse<>(false, message, null, errorCode.getCode(), null);
    }

    public static <T> ApiResponse<T> failure(ErrorCode errorCode, String message, Map<String, String> details) {
        return new ApiResponse<>(false, message, null, errorCode.getCode(), details);
    }
}
