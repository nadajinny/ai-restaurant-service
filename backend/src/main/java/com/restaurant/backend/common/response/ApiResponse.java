package com.restaurant.backend.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.restaurant.backend.common.exception.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        @Schema(description = "요청 성공 여부", example = "true")
        boolean success,
        @Schema(description = "응답 메시지", example = "요청이 성공했습니다.")
        String message,
        @Schema(description = "응답 데이터")
        T data,
        @Schema(description = "에러 코드", example = "INVALID_INPUT_VALUE", nullable = true)
        String errorCode,
        @Schema(
                description = "필드별 오류 상세 정보",
                example = "{\"message\":\"message는 필수입니다.\"}",
                nullable = true
        )
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
