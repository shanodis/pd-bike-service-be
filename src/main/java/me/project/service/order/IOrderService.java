package me.project.service.order;

import me.project.dtos.request.PageRequestDTO;
import me.project.dtos.request.order.OrderCreateRequestDTO;
import me.project.dtos.request.orderPart.OrderPartUpdateRequestDTO;
import me.project.dtos.request.orderService.OrderServiceCreateRequestDTO;
import me.project.dtos.response.order.OrderPaginationResponseDTO;
import me.project.dtos.response.page.PageResponse;
import me.project.entitiy.Order;
import me.project.entitiy.User;

import java.time.LocalDateTime;
import java.util.UUID;

public interface IOrderService {
    Order getLatestByUser(User user);

    Order getById(UUID orderId);

    PageResponse<OrderPaginationResponseDTO> getOrders(PageRequestDTO requestDTO,
                                                       String phrase,
                                                       LocalDateTime orderDateFrom,
                                                       LocalDateTime orderDateTo,
                                                       UUID orderStatusId,
                                                       UUID userId);

    UUID createOrder(OrderCreateRequestDTO request);

    void addOrderPartToOrder(UUID orderId, UUID orderPartId);

    UUID addOrderServiceToOrder(UUID orderId, OrderServiceCreateRequestDTO request);

    void completePayment(UUID orderId);

    void updateOrdersOrderPart(UUID orderId, UUID orderPartId, OrderPartUpdateRequestDTO request);

    void deleteOrdersOrderPart(UUID orderId, UUID orderPartId);

    void deleteOrdersOrderService(UUID orderId, UUID orderServiceId);

    void updateOrderService(UUID orderId, UUID orderStatusId);

    void updateOrderNote(UUID orderId, String note);
}
