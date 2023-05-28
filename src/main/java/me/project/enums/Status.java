package me.project.enums;

import me.project.entitiy.OrderStatus;

public enum Status {
    TODO(new OrderStatus());
    private final OrderStatus orderStatus;


    Status(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }
}
