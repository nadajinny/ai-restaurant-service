package com.restaurant.backend.payment.service;

import com.restaurant.backend.payment.domain.PaymentStatus;
import com.restaurant.backend.payment.dto.MockPaymentResult;
import org.springframework.stereotype.Component;

@Component
public class MockPaymentGateway {

    public PaymentStatus process(MockPaymentResult mockResult) {
        if (mockResult == null || mockResult == MockPaymentResult.APPROVED) {
            return PaymentStatus.APPROVED;
        }

        return PaymentStatus.FAILED;
    }
}
