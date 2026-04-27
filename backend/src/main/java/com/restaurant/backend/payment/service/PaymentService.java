package com.restaurant.backend.payment.service;

import com.restaurant.backend.common.exception.BusinessException;
import com.restaurant.backend.common.exception.ErrorCode;
import com.restaurant.backend.inventory.service.InventoryService;
import com.restaurant.backend.notification.service.NotificationService;
import com.restaurant.backend.order.domain.Order;
import com.restaurant.backend.order.domain.OrderStatus;
import com.restaurant.backend.order.domain.OrderStatusHistory;
import com.restaurant.backend.order.repository.OrderRepository;
import com.restaurant.backend.order.repository.OrderStatusHistoryRepository;
import com.restaurant.backend.payment.domain.Payment;
import com.restaurant.backend.payment.domain.PaymentStatus;
import com.restaurant.backend.payment.dto.PaymentCreateRequest;
import com.restaurant.backend.payment.dto.PaymentResponse;
import com.restaurant.backend.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    private static final String MOCK_PAYMENT_SYSTEM = "mock-payment";

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final InventoryService inventoryService;
    private final NotificationService notificationService;
    private final MockPaymentGateway mockPaymentGateway;

    public PaymentService(
            PaymentRepository paymentRepository,
            OrderRepository orderRepository,
            OrderStatusHistoryRepository orderStatusHistoryRepository,
            InventoryService inventoryService,
            NotificationService notificationService,
            MockPaymentGateway mockPaymentGateway
    ) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.orderStatusHistoryRepository = orderStatusHistoryRepository;
        this.inventoryService = inventoryService;
        this.notificationService = notificationService;
        this.mockPaymentGateway = mockPaymentGateway;
    }

    @Transactional
    public PaymentResponse createPayment(PaymentCreateRequest request) {
        Order order = getOrderById(request.orderId());
        validatePaymentCreatable(order);

        Payment payment = paymentRepository.save(Payment.create(order, order.getTotalPrice(), PaymentStatus.REQUESTED));
        PaymentStatus result = mockPaymentGateway.process(request.mockResult());

        if (result == PaymentStatus.APPROVED) {
            payment.approve();
            return toResponse(payment);
        }

        payment.fail();
        cancelOrderForPaymentFailure(order);
        return toResponse(payment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPayment(Long paymentId) {
        return toResponse(getPaymentById(paymentId));
    }

    @Transactional
    public PaymentResponse cancelPayment(Long paymentId) {
        Payment payment = getPaymentById(paymentId);

        if (payment.getStatus() == PaymentStatus.CANCELED) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "이미 취소된 결제입니다.");
        }

        if (payment.getStatus() == PaymentStatus.FAILED) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "실패한 결제는 취소할 수 없습니다.");
        }

        Order order = payment.getOrder();
        if (order.getStatus() != OrderStatus.RECEIVED && order.getStatus() != OrderStatus.CANCELED) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "진행 중이거나 완료된 주문의 결제는 취소할 수 없습니다.");
        }

        payment.cancel();
        if (order.getStatus() == OrderStatus.RECEIVED) {
            cancelOrder(order);
        }

        return toResponse(payment);
    }

    private void validatePaymentCreatable(Order order) {
        if (order.getStatus() != OrderStatus.RECEIVED) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "RECEIVED 상태의 주문만 결제할 수 있습니다.");
        }

        if (paymentRepository.findByOrder_Id(order.getId()).isPresent()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "이미 결제가 생성된 주문입니다.");
        }
    }

    private void cancelOrderForPaymentFailure(Order order) {
        if (order.getStatus() == OrderStatus.RECEIVED) {
            cancelOrder(order);
        }
    }

    private void cancelOrder(Order order) {
        OrderStatus currentStatus = order.getStatus();
        order.changeStatus(OrderStatus.CANCELED);
        inventoryService.restoreInventoryForCanceledOrder(order);
        orderStatusHistoryRepository.save(
                OrderStatusHistory.create(order, currentStatus, OrderStatus.CANCELED, MOCK_PAYMENT_SYSTEM)
        );
        notificationService.createOrderStatusChangedNotification(order, currentStatus, OrderStatus.CANCELED);
    }

    private Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
    }

    private Payment getPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));
    }

    private PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getOrder().getId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }
}
