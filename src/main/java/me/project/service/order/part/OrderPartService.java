package me.project.service.order.part;

import me.project.dtos.request.orderPart.OrderPartCreateDTO;
import me.project.entitiy.Order;
import me.project.entitiy.OrderPart;
import me.project.repository.OrderPartRepository;
import me.project.repository.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@AllArgsConstructor
public class OrderPartService implements IOrderPartService {
    private final OrderPartRepository orderPartRepository;
    private final OrderRepository orderRepository;

    private String orderPartNotFound(UUID orderPartId) {
        return String.format("Order Part with id %s not found", orderPartId);
    }

    private String orderNotFound(UUID orderId) {
        return String.format("Order with id %s not found", orderId);
    }

    public OrderPart getOrderPartById(UUID orderPartId) {
        return orderPartRepository.findById(orderPartId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, orderPartNotFound(orderPartId))
        );
    }

    public UUID createOrderPart(UUID orderId, OrderPartCreateDTO request) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, orderNotFound(orderId))
        );

        OrderPart orderPart = new OrderPart();

        Order tmp = new Order();
        tmp.setOrderId(order.getOrderId());
        orderPart.setOrder(tmp);

        orderPart.setOrderCode(request.getOrderCode().trim());
        orderPart.setOrderName(request.getOrderName().trim());
        orderPart.setOrderPrice(request.getOrderPrice());

        orderPartRepository.save(orderPart);

        return orderPart.getOrderPartId();
    }
}
