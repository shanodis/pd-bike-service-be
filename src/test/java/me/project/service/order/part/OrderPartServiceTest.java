package me.project.service.order.part;

import me.project.dtos.request.orderPart.OrderPartCreateDTO;
import me.project.entitiy.Order;
import me.project.entitiy.OrderPart;
import me.project.repository.OrderPartRepository;
import me.project.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderPartServiceTest {
    @Mock
    private OrderPartRepository orderPartRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderPartService orderPartService;


    @Test
    @DisplayName("Should throw a ResponseStatusException with HttpStatus.NOT_FOUND when the order part id is not found")
    void getOrderPartByIdWhenIdIsNotFoundThenThrowResponseStatusException() {
        UUID orderPartId = UUID.randomUUID();

        when(orderPartRepository.findById(orderPartId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> {
            orderPartService.getOrderPartById(orderPartId);
        });

        verify(orderPartRepository, times(1)).findById(orderPartId);
    }

    @Test
    @DisplayName("Should return the order part when the order part id is valid")
    void getOrderPartByIdWhenIdIsValid() {
        UUID orderPartId = UUID.randomUUID();
        OrderPart orderPart = new OrderPart();
        orderPart.setOrderPartId(orderPartId);
        when(orderPartRepository.findById(orderPartId)).thenReturn(Optional.of(orderPart));

        OrderPart result = orderPartService.getOrderPartById(orderPartId);

        assertNotNull(result);
        verify(orderPartRepository, times(1)).findById(orderPartId);
    }

    @Test
    @DisplayName("Should throw an exception when the order does not exist")
    void createOrderPartWhenOrderDoesNotExistThenThrowException() {
        UUID orderId = UUID.randomUUID();
        OrderPartCreateDTO request = new OrderPartCreateDTO("code", "name", BigDecimal.valueOf(10.0));

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> orderPartService.createOrderPart(orderId, request));
        verify(orderRepository, times(1)).findById(orderId);
        verifyNoInteractions(orderPartRepository);
    }

    @Test
    @DisplayName("Should create an order part when the order exists")
    void createOrderPartWhenOrderExists() {
        UUID orderId = UUID.randomUUID();
        OrderPartCreateDTO request = new OrderPartCreateDTO(
                "orderCode",
                "orderName",
                BigDecimal.valueOf(100.0)
        );
        Order order = new Order();
        order.setOrderId(orderId);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderPartRepository.save(any(OrderPart.class))).thenAnswer(invocation -> {
            OrderPart orderPart = invocation.getArgument(0);
            orderPart.setOrderPartId(UUID.randomUUID());
            return orderPart;
        });

        UUID orderPartId = orderPartService.createOrderPart(orderId, request);

        assertNotNull(orderPartId);
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderPartRepository, times(1)).save(any(OrderPart.class));
    }
}