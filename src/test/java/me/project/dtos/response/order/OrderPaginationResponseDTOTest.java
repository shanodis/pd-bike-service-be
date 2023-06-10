package me.project.dtos.response.order;

import me.project.entitiy.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("OrderPaginationResponseDTO tests")
class OrderPaginationResponseDTOTest {

    @Test
    @DisplayName("Should convert Order entity to OrderPaginationResponseDTO")
    void convertFromEntityToOrderPaginationResponseDTO() {
        // Create a mock Order object
        Order order = mock(Order.class);
        UUID orderId = UUID.randomUUID();

        Bike bike = new Bike();
        bike.setBikeName("BikeName");
        bike.setBikeModel("BikeModel");
        LocalDateTime createdOn = LocalDateTime.now();
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        OrderService orderService1 = new OrderService();
        Service service1 = new Service();
        service1.setServiceName("Service1");
        orderService1.setService(service1);
        OrderService orderService2 = new OrderService();
        Service service2 = new Service();
        service2.setServiceName("Service2");
        orderService2.setService(service2);
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderStatusName("Pending");

        when(order.getOrderId()).thenReturn(orderId);
        when(order.getBike()).thenReturn(bike);
        when(order.getCreatedOn()).thenReturn(createdOn);
        when(order.getUser()).thenReturn(user);
        when(order.getOrderServices()).thenReturn(Arrays.asList(orderService1, orderService2));
        when(order.getOrderStatus()).thenReturn(orderStatus);

        // Call the convertFromEntity method with the mock Order object
        OrderPaginationResponseDTO orderPaginationResponseDTO = OrderPaginationResponseDTO.convertFromEntity(order);

        // Verify that the OrderPaginationResponseDTO object was created with the correct values
        assertEquals(orderId, orderPaginationResponseDTO.getOrderId());
        assertEquals("BikeName", orderPaginationResponseDTO.getBikeName());
        assertEquals("BikeModel", orderPaginationResponseDTO.getBikeModel());
        assertEquals(createdOn, orderPaginationResponseDTO.getCreatedOn());
        assertEquals(user.getUserId(), orderPaginationResponseDTO.getUserId());
        assertEquals("Pending", orderPaginationResponseDTO.getOrderStatusName());
        assertEquals("Service1", orderPaginationResponseDTO.getServicesNames().get(0));
        assertEquals("Service2", orderPaginationResponseDTO.getServicesNames().get(1));
    }
}