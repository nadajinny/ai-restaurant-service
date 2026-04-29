package com.restaurant.backend.review.repository;

import com.restaurant.backend.review.domain.Review;
import com.restaurant.backend.review.domain.ReviewStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByUser_IdAndOrder_IdAndMenu_Id(Long userId, Long orderId, Long menuId);

    List<Review> findAllByMenu_IdAndStatusOrderByCreatedAtDescIdDesc(Long menuId, ReviewStatus status);

    List<Review> findAllByOrderByCreatedAtDescIdDesc();

    Optional<Review> findById(Long reviewId);
}
