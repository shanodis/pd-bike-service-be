package me.project.enums;

import me.project.entitiy.OrderStatus;

import java.util.UUID;

public enum Status {
    TODO(new OrderStatus(UUID.fromString("7ae4611f-3beb-4b54-b6c8-d2851fe0dc83"),"todo")),
    RECEIVED(new OrderStatus(UUID.fromString("be8e1aec-928e-45c3-8931-8665221fd8fe"),"received")),
    DONE(new OrderStatus(UUID.fromString("3f903739-a361-497f-bdee-2fb0b8a9229e"),"done")),
    InPROGRESS(new OrderStatus(UUID.fromString("9b858840-f910-488d-b2b2-3642b112b226"),"in-progress")),
    ;
    private final OrderStatus orderStatus;

    Status(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }
}
