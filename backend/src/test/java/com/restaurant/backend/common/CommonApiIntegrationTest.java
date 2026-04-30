package com.restaurant.backend.common;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.webjars.WebJarVersionLocator;

@SpringBootTest
@AutoConfigureMockMvc
class CommonApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void healthCheckReturnsSuccessResponse() throws Exception {
        mockMvc.perform(get("/api/v1/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("헬스 체크가 성공했습니다."))
                .andExpect(jsonPath("$.data.status").value("UP"))
                .andExpect(jsonPath("$.data.service").value("backend"));
    }

    @Test
    void validationFailureReturnsCommonErrorResponse() throws Exception {
        mockMvc.perform(post("/api/v1/ai/recommendations/mock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"message\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("입력값이 올바르지 않습니다."))
                .andExpect(jsonPath("$.errorCode").value("INVALID_INPUT_VALUE"))
                .andExpect(jsonPath("$.details.message").value("message는 필수입니다."));
    }

    @Test
    void openApiSpecEndpointReturnsSuccessResponse() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openapi").value("3.1.0"))
                .andExpect(jsonPath("$.info.title").value("AI Restaurant Service API"));
    }

    @Test
    void swaggerUiEndpointReturnsSuccessResponse() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }

    @Test
    void swaggerUiAssetsEndpointReturnsSuccessResponse() throws Exception {
        String swaggerUiVersion = new WebJarVersionLocator().version("swagger-ui");

        mockMvc.perform(get("/webjars/swagger-ui/" + swaggerUiVersion + "/swagger-ui-bundle.js"))
                .andExpect(status().isOk());
    }

    @Test
    void unknownEndpointReturnsNotFoundResponse() throws Exception {
        mockMvc.perform(get("/api/v1/unknown"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("요청한 리소스를 찾을 수 없습니다."))
                .andExpect(jsonPath("$.errorCode").value("RESOURCE_NOT_FOUND"));
    }
}
