package com.restaurant.backend.order.service;

import com.restaurant.backend.order.dto.OrderSummaryDto;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    public OrderSummaryDto getSampleOrder() {
        return new OrderSummaryDto(1L, "RECEIVED", 18000);
    }
}
