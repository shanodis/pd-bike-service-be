package me.project.dtos.response.order;

import me.project.entitiy.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("OrderInvoiceDTO should")
class OrderInvoiceDTOTest {

    @Test
    @DisplayName("Should convert Order entity to OrderInvoiceDTO with correct values")
    void convertFromEntityToOrderInvoiceDTOWithCorrectValues() {        // Create mock objects
        Order order = mock(Order.class);
        OrderStatus orderStatus = mock(OrderStatus.class);
        OrderPart orderPart = mock(OrderPart.class);
        OrderService orderService = mock(OrderService.class);

        // Set up mock objects
        UUID orderId = UUID.randomUUID();
        UUID orderStatusId = UUID.randomUUID();
        UUID orderPartId = UUID.randomUUID();
        UUID orderServiceId = UUID.randomUUID();
        LocalDateTime createdOn = LocalDateTime.now();
        String note = "Test note";
        String orderStatusName = "Test order status name";
        String orderCode = "Test order code";
        String orderName = "Test order name";
        BigDecimal orderPrice = new BigDecimal(10);
        BigDecimal servicePrice = new BigDecimal(20);
        Boolean isPayed = true;

        when(order.getOrderId()).thenReturn(orderId);
        when(order.getOrderStatus()).thenReturn(orderStatus);
        when(order.getOrderParts()).thenReturn(List.of(orderPart));
        when(order.getOrderServices()).thenReturn(List.of(orderService));
        when(order.getCreatedOn()).thenReturn(createdOn);
        when(order.getNote()).thenReturn(note);
        when(order.getIsPayed()).thenReturn(isPayed);

        when(orderStatus.getOrderStatusId()).thenReturn(orderStatusId);
        when(orderStatus.getOrderStatusName()).thenReturn(orderStatusName);

        when(orderPart.getOrderPartId()).thenReturn(orderPartId);
        when(orderPart.getOrderCode()).thenReturn(orderCode);
        when(orderPart.getOrderName()).thenReturn(orderName);
        when(orderPart.getOrderPrice()).thenReturn(orderPrice);

        Service service = mock(Service.class);
        when(service.getServicePrice()).thenReturn(servicePrice);

        when(orderService.getOrderServiceId()).thenReturn(orderServiceId);
        when(orderService.getService()).thenReturn(service);

        // Call the method under test
        OrderInvoiceDTO orderInvoiceDTO = OrderInvoiceDTO.convertFromEntity(order);

        // Verify the result
        assertEquals(order.getNote(), orderInvoiceDTO.getNote());
        assertEquals(orderStatus.getOrderStatusId(), orderInvoiceDTO.getOrderStatusId());
        assertEquals(orderStatus.getOrderStatusName(), orderInvoiceDTO.getOrderStatusName());
        assertEquals(order.getCreatedOn(), orderInvoiceDTO.getCreatedOn());
        assertEquals(isPayed, orderInvoiceDTO.getIsPaid());

        assertEquals(1, orderInvoiceDTO.getOrderParts().size());
        OrderPartDTO orderPartDTO = orderInvoiceDTO.getOrderParts().get(0);
        assertEquals(orderPartId, orderPartDTO.getOrderPartId());
        assertEquals(orderCode, orderPartDTO.getOrderCode());
        assertEquals(orderName, orderPartDTO.getOrderName());
        assertEquals(orderPrice, orderPartDTO.getOrderPrice());

        assertEquals(1, orderInvoiceDTO.getOrderServices().size());
        OrderServiceDTO orderServiceDTO = orderInvoiceDTO.getOrderServices().get(0);
        assertEquals(orderServiceId, orderServiceDTO.getOrderServiceId());
        assertEquals(service.getServiceId(), orderServiceDTO.getServiceId());
        assertEquals(service.getServiceName(), orderServiceDTO.getServiceName());
        assertEquals(servicePrice, orderServiceDTO.getOrderPrice());

        assertEquals(orderPrice.add(servicePrice), orderInvoiceDTO.getTotalPrice());
    }

}