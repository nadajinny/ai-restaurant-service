package com.restaurant.backend.review.service;

import com.restaurant.backend.common.cache.CacheInvalidationService;
import com.restaurant.backend.common.exception.BusinessException;
import com.restaurant.backend.common.exception.ErrorCode;
import com.restaurant.backend.menu.domain.Menu;
import com.restaurant.backend.menu.repository.MenuRepository;
import com.restaurant.backend.order.domain.Order;
import com.restaurant.backend.order.domain.OrderItem;
import com.restaurant.backend.order.repository.OrderRepository;
import com.restaurant.backend.review.domain.Review;
import com.restaurant.backend.review.domain.ReviewStatus;
import com.restaurant.backend.review.dto.MenuReviewResponse;
import com.restaurant.backend.review.dto.ReviewCreateRequest;
import com.restaurant.backend.review.dto.ReviewResponse;
import com.restaurant.backend.review.dto.ReviewUpdateRequest;
import com.restaurant.backend.review.repository.ReviewRepository;
import com.restaurant.backend.user.domain.User;
import com.restaurant.backend.user.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final MenuRepository menuRepository;
    private final ReviewMapper reviewMapper;
    private final CacheInvalidationService cacheInvalidationService;

    public ReviewService(
            ReviewRepository reviewRepository,
            UserRepository userRepository,
            OrderRepository orderRepository,
            MenuRepository menuRepository,
            ReviewMapper reviewMapper,
            CacheInvalidationService cacheInvalidationService
    ) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.menuRepository = menuRepository;
        this.reviewMapper = reviewMapper;
        this.cacheInvalidationService = cacheInvalidationService;
    }

    @Transactional
    public ReviewResponse createReview(Long userId, ReviewCreateRequest request) {
        User user = getUserById(userId);
        Order order = getOrderById(request.orderId());
        Menu menu = getMenuById(request.menuId());

        validateOrderOwnership(order, user);
        validateOrderedMenu(order, menu.getId());
        validateDuplicateReview(userId, order.getId(), menu.getId());

        Review review = reviewRepository.save(Review.create(
                user,
                menu,
                order,
                request.content(),
                request.rating(),
                request.aiGenerated(),
                ReviewStatus.ACTIVE
        ));

        cacheInvalidationService.evictReviewRelatedCaches(menu.getId());
        cacheInvalidationService.evictUserPersonalizedRecommendations(userId);
        return reviewMapper.toReviewResponse(review);
    }

    @Transactional(readOnly = true)
    public List<MenuReviewResponse> getMenuReviews(Long menuId) {
        getMenuById(menuId);

        return reviewRepository.findAllByMenu_IdAndStatusOrderByCreatedAtDescIdDesc(menuId, ReviewStatus.ACTIVE).stream()
                .map(reviewMapper::toMenuReviewResponse)
                .toList();
    }

    @Transactional
    public ReviewResponse updateReview(Long userId, Long reviewId, ReviewUpdateRequest request) {
        User user = getUserById(userId);
        Review review = getReviewById(reviewId);

        validateReviewOwnership(review, user);
        validateReviewNotDeleted(review);

        review.update(request.content(), request.rating(), request.aiGenerated());
        cacheInvalidationService.evictReviewRelatedCaches(review.getMenu().getId());
        cacheInvalidationService.evictUserPersonalizedRecommendations(userId);
        return reviewMapper.toReviewResponse(review);
    }

    @Transactional
    public void deleteReview(Long userId, Long reviewId) {
        User user = getUserById(userId);
        Review review = getReviewById(reviewId);

        validateReviewOwnership(review, user);
        validateReviewNotDeleted(review);
        review.delete();
        cacheInvalidationService.evictReviewRelatedCaches(review.getMenu().getId());
        cacheInvalidationService.evictUserPersonalizedRecommendations(userId);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getAdminReviews() {
        // TODO: 관리자 권한 검증은 인증 기능 구현 후 적용한다.
        return reviewRepository.findAllByOrderByCreatedAtDescIdDesc().stream()
                .map(reviewMapper::toReviewResponse)
                .toList();
    }

    @Transactional
    public ReviewResponse hideReview(Long reviewId) {
        // TODO: 관리자 권한 검증은 인증 기능 구현 후 적용한다.
        Review review = getReviewById(reviewId);
        if (review.getStatus() == ReviewStatus.DELETED) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "삭제된 리뷰는 숨김 처리할 수 없습니다.");
        }

        review.hide();
        cacheInvalidationService.evictReviewRelatedCaches(review.getMenu().getId());
        return reviewMapper.toReviewResponse(review);
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    private Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
    }

    private Menu getMenuById(Long menuId) {
        return menuRepository.findById(menuId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MENU_NOT_FOUND));
    }

    private Review getReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));
    }

    private void validateOrderOwnership(Order order, User user) {
        if (!order.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "자신의 주문에 대해서만 리뷰를 작성할 수 있습니다.");
        }
    }

    private void validateOrderedMenu(Order order, Long menuId) {
        boolean orderedMenu = order.getOrderItems().stream()
                .map(OrderItem::getMenu)
                .anyMatch(menu -> menu.getId().equals(menuId));

        if (!orderedMenu) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "주문한 메뉴에 대해서만 리뷰를 작성할 수 있습니다.");
        }
    }

    private void validateDuplicateReview(Long userId, Long orderId, Long menuId) {
        if (reviewRepository.existsByUser_IdAndOrder_IdAndMenu_Id(userId, orderId, menuId)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "하나의 주문 메뉴에는 하나의 리뷰만 작성할 수 있습니다.");
        }
    }

    private void validateReviewOwnership(Review review, User user) {
        if (!review.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "자신의 리뷰만 수정하거나 삭제할 수 있습니다.");
        }
    }

    private void validateReviewNotDeleted(Review review) {
        if (review.getStatus() == ReviewStatus.DELETED) {
            throw new BusinessException(ErrorCode.REVIEW_NOT_FOUND);
        }
    }
}
