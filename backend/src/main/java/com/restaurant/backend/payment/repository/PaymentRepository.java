package com.restaurant.backend.payment.repository;

import com.restaurant.backend.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
