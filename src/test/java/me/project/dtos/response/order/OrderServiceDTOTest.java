package me.project.dtos.response.order;

import me.project.entitiy.OrderService;
import me.project.entitiy.Service;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("OrderServiceDTO")
class OrderServiceDTOTest {

    @Test
    @DisplayName("Should convert OrderService entity to OrderServiceDTO")
    void convertFromEntityToOrderServiceDTO() {
        OrderService orderService = new OrderService();
        Service service = new Service("Service 1", new BigDecimal("10.00"));
        service.setServiceId(UUID.randomUUID());
        orderService.setOrderServiceId(UUID.randomUUID());
        orderService.setService(service);

        OrderServiceDTO orderServiceDTO = OrderServiceDTO.convertFromEntity(orderService);

        assertNotNull(orderServiceDTO);
        assertEquals(orderService.getOrderServiceId(), orderServiceDTO.getOrderServiceId());
        assertEquals(service.getServiceId(), orderServiceDTO.getServiceId());
        assertEquals(service.getServiceName(), orderServiceDTO.getServiceName());
        assertEquals(service.getServicePrice(), orderServiceDTO.getOrderPrice());
    }

}