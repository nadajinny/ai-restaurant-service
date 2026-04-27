package com.restaurant.backend.payment.service;

import com.restaurant.backend.payment.dto.PaymentSummaryDto;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    public PaymentSummaryDto getSamplePayment() {
        return new PaymentSummaryDto(1L, 1L, 18000, "PENDING");
    }
}
