package me.project.service.order;

import me.project.dtos.request.PageRequestDTO;
import me.project.dtos.request.order.OrderCreateRequestDTO;
import me.project.dtos.response.order.OrderPaginationResponseDTO;
import me.project.dtos.response.page.PageResponse;
import me.project.entitiy.Order;
import me.project.entitiy.User;

import java.util.UUID;

public interface IOrderService {
    Order getLatestByUser(User user);

    Order getById(UUID orderId);

    PageResponse<OrderPaginationResponseDTO> getOrders(PageRequestDTO requestDTO, String phrase);

    UUID createOrder(OrderCreateRequestDTO request);

    void addOrderPartToOrder(UUID orderId, UUID orderPartId);

    void updateOrdersOrderPart(UUID orderId, UUID orderPartId, UUID newOrderPartId);

    void deleteOrdersOrderPart(UUID orderId, UUID orderPartId);
}
