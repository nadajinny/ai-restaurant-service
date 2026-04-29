package com.restaurant.backend.coupon.service;

import com.restaurant.backend.common.exception.BusinessException;
import com.restaurant.backend.common.exception.ErrorCode;
import com.restaurant.backend.coupon.domain.Coupon;
import com.restaurant.backend.coupon.domain.CouponUsage;
import com.restaurant.backend.coupon.dto.AdminCouponRequest;
import com.restaurant.backend.coupon.dto.AvailableCouponResponse;
import com.restaurant.backend.coupon.dto.CouponApplyRequest;
import com.restaurant.backend.coupon.dto.CouponApplyResponse;
import com.restaurant.backend.coupon.dto.CouponResponse;
import com.restaurant.backend.coupon.repository.CouponRepository;
import com.restaurant.backend.coupon.repository.CouponUsageRepository;
import com.restaurant.backend.order.domain.Order;
import com.restaurant.backend.order.domain.OrderStatus;
import com.restaurant.backend.order.repository.OrderRepository;
import com.restaurant.backend.payment.repository.PaymentRepository;
import com.restaurant.backend.user.domain.User;
import com.restaurant.backend.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponUsageRepository couponUsageRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    public CouponService(
            CouponRepository couponRepository,
            CouponUsageRepository couponUsageRepository,
            OrderRepository orderRepository,
            PaymentRepository paymentRepository,
            UserRepository userRepository
    ) {
        this.couponRepository = couponRepository;
        this.couponUsageRepository = couponUsageRepository;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public CouponResponse createCoupon(AdminCouponRequest request) {
        validateCouponRequest(request, null);

        Coupon coupon = couponRepository.save(Coupon.create(
                request.code(),
                request.name(),
                request.discountAmount(),
                request.discountRate(),
                request.maxDiscountAmount(),
                request.minOrderAmount(),
                request.availableFrom(),
                request.availableTo(),
                request.availableCount(),
                request.active()
        ));

        return toCouponResponse(coupon);
    }

    @Transactional
    public CouponResponse updateCoupon(Long couponId, AdminCouponRequest request) {
        validateCouponRequest(request, couponId);
        Coupon coupon = getCouponById(couponId);
        coupon.update(
                request.code(),
                request.name(),
                request.discountAmount(),
                request.discountRate(),
                request.maxDiscountAmount(),
                request.minOrderAmount(),
                request.availableFrom(),
                request.availableTo(),
                request.availableCount(),
                request.active()
        );
        return toCouponResponse(coupon);
    }

    @Transactional
    public CouponResponse disableCoupon(Long couponId) {
        Coupon coupon = getCouponById(couponId);
        coupon.disable();
        return toCouponResponse(coupon);
    }

    @Transactional(readOnly = true)
    public List<CouponResponse> getCoupons() {
        return couponRepository.findAll().stream()
                .sorted(java.util.Comparator.comparing(Coupon::getCreatedAt).reversed()
                        .thenComparing(Coupon::getId, java.util.Comparator.reverseOrder()))
                .map(this::toCouponResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AvailableCouponResponse> getAvailableCoupons() {
        LocalDateTime now = LocalDateTime.now();

        return couponRepository.findAllByActiveTrueAndAvailableFromLessThanEqualAndAvailableToGreaterThanEqual(now, now).stream()
                .filter(coupon -> couponUsageRepository.countByCoupon_Id(coupon.getId()) < coupon.getAvailableCount())
                .map(coupon -> new AvailableCouponResponse(
                        coupon.getId(),
                        coupon.getCode(),
                        coupon.getName(),
                        coupon.getDiscountAmount(),
                        coupon.getDiscountRate(),
                        coupon.getMaxDiscountAmount(),
                        coupon.getMinOrderAmount(),
                        coupon.getAvailableFrom(),
                        coupon.getAvailableTo(),
                        coupon.getAvailableCount(),
                        couponUsageRepository.countByCoupon_Id(coupon.getId())
                ))
                .toList();
    }

    @Transactional
    public CouponApplyResponse applyCoupon(Long userId, CouponApplyRequest request) {
        // TODO: 인증 기능 구현 후 userId 요청 파라미터 대신 JWT 기반 사용자 식별로 대체한다.
        User user = getUserById(userId);
        Order order = getOrderById(request.orderId());
        Coupon coupon = couponRepository.findByCode(request.couponCode())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "쿠폰 코드를 찾을 수 없습니다."));

        validateCouponApplicable(coupon, user, order);

        int discountAmount = calculateDiscount(order.getOriginalTotalPrice(), coupon);
        order.applyDiscount(discountAmount);
        couponUsageRepository.save(CouponUsage.create(coupon, user, order, discountAmount));

        return new CouponApplyResponse(
                order.getId(),
                coupon.getId(),
                coupon.getCode(),
                order.getOriginalTotalPrice(),
                discountAmount,
                order.getTotalPrice()
        );
    }

    private void validateCouponRequest(AdminCouponRequest request, Long currentCouponId) {
        couponRepository.findByCode(request.code()).ifPresent(existingCoupon -> {
            if (currentCouponId == null || !existingCoupon.getId().equals(currentCouponId)) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "이미 존재하는 쿠폰 코드입니다.");
            }
        });

        if (request.availableFrom().isAfter(request.availableTo())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "사용 가능 시작일은 종료일보다 늦을 수 없습니다.");
        }

        boolean hasAmount = request.discountAmount() != null && request.discountAmount() > 0;
        boolean hasRate = request.discountRate() != null && request.discountRate() > 0;

        if (hasAmount == hasRate) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "할인 금액 또는 할인율 중 하나만 설정해야 합니다.");
        }

        if (hasRate && request.discountRate() > 100) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "discountRate는 100 이하여야 합니다.");
        }
    }

    private void validateCouponApplicable(Coupon coupon, User user, Order order) {
        if (!order.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "자신의 주문에만 쿠폰을 적용할 수 있습니다.");
        }

        if (order.getStatus() != OrderStatus.RECEIVED) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "RECEIVED 상태의 주문에만 쿠폰을 적용할 수 있습니다.");
        }

        if (couponUsageRepository.existsByOrder_Id(order.getId())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "이미 쿠폰이 적용된 주문입니다.");
        }

        if (paymentRepository.findByOrder_Id(order.getId()).isPresent()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "이미 결제가 생성된 주문에는 쿠폰을 적용할 수 없습니다.");
        }

        if (!coupon.isActive()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "비활성화된 쿠폰입니다.");
        }

        LocalDateTime now = LocalDateTime.now();
        if (coupon.getAvailableFrom().isAfter(now) || coupon.getAvailableTo().isBefore(now)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "사용 가능 기간이 아닌 쿠폰입니다.");
        }

        if (order.getOriginalTotalPrice() < coupon.getMinOrderAmount()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "최소 주문 금액을 만족하지 않습니다.");
        }

        if (couponUsageRepository.countByCoupon_Id(coupon.getId()) >= coupon.getAvailableCount()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "남은 사용 가능 횟수가 없습니다.");
        }

        if (couponUsageRepository.existsByCoupon_IdAndUser_Id(coupon.getId(), user.getId())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "이미 사용한 쿠폰입니다.");
        }
    }

    private int calculateDiscount(int orderAmount, Coupon coupon) {
        int discountAmount;

        if (coupon.getDiscountAmount() != null && coupon.getDiscountAmount() > 0) {
            discountAmount = coupon.getDiscountAmount();
        } else {
            discountAmount = orderAmount * coupon.getDiscountRate() / 100;
            if (coupon.getMaxDiscountAmount() != null) {
                discountAmount = Math.min(discountAmount, coupon.getMaxDiscountAmount());
            }
        }

        return Math.min(orderAmount, discountAmount);
    }

    private Coupon getCouponById(Long couponId) {
        return couponRepository.findById(couponId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "쿠폰을 찾을 수 없습니다."));
    }

    private Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    private CouponResponse toCouponResponse(Coupon coupon) {
        return new CouponResponse(
                coupon.getId(),
                coupon.getCode(),
                coupon.getName(),
                coupon.getDiscountAmount(),
                coupon.getDiscountRate(),
                coupon.getMaxDiscountAmount(),
                coupon.getMinOrderAmount(),
                coupon.getAvailableFrom(),
                coupon.getAvailableTo(),
                coupon.getAvailableCount(),
                coupon.isActive()
        );
    }
}
