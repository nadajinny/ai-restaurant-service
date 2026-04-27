package com.restaurant.backend.review.repository;

import com.restaurant.backend.review.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
