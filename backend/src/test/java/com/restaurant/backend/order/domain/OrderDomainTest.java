package com.restaurant.backend.order.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.restaurant.backend.user.domain.User;
import com.restaurant.backend.user.domain.UserRole;
import org.junit.jupiter.api.Test;

class OrderDomainTest {

    @Test
    void canTransitionToFollowsBusinessRules() {
        User user = User.create("user01", "password", "테스트 사용자", UserRole.USER);

        Order receivedOrder = Order.create(user, 10000, OrderStatus.RECEIVED);
        assertThat(receivedOrder.canTransitionTo(OrderStatus.COOKING)).isTrue();
        assertThat(receivedOrder.canTransitionTo(OrderStatus.CANCELED)).isTrue();
        assertThat(receivedOrder.canTransitionTo(OrderStatus.READY)).isFalse();
        assertThat(receivedOrder.canTransitionTo(OrderStatus.COMPLETED)).isFalse();

        Order cookingOrder = Order.create(user, 10000, OrderStatus.COOKING);
        assertThat(cookingOrder.canTransitionTo(OrderStatus.READY)).isTrue();
        assertThat(cookingOrder.canTransitionTo(OrderStatus.CANCELED)).isFalse();

        Order readyOrder = Order.create(user, 10000, OrderStatus.READY);
        assertThat(readyOrder.canTransitionTo(OrderStatus.COMPLETED)).isTrue();
        assertThat(readyOrder.canTransitionTo(OrderStatus.COOKING)).isFalse();

        Order completedOrder = Order.create(user, 10000, OrderStatus.COMPLETED);
        assertThat(completedOrder.canTransitionTo(OrderStatus.CANCELED)).isFalse();

        Order canceledOrder = Order.create(user, 10000, OrderStatus.CANCELED);
        assertThat(canceledOrder.canTransitionTo(OrderStatus.RECEIVED)).isFalse();
    }

    @Test
    void changeStatusUpdatesCurrentStatus() {
        User user = User.create("user01", "password", "테스트 사용자", UserRole.USER);
        Order order = Order.create(user, 10000, OrderStatus.RECEIVED);

        order.changeStatus(OrderStatus.COOKING);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.COOKING);
    }
}
