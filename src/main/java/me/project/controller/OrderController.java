package me.project.controller;


import me.project.dtos.request.PageRequestDTO;
import me.project.dtos.request.order.OrderCreateRequestDTO;
import me.project.dtos.request.orderPart.OrderPartCreateDTO;
import me.project.dtos.request.orderPart.OrderPartUpdateRequestDTO;
import me.project.dtos.request.orderService.OrderServiceCreateRequestDTO;
import me.project.dtos.response.order.OrderPaymentDTO;
import me.project.dtos.response.order.OrderInformationDTO;
import me.project.dtos.response.order.OrderInvoiceDTO;
import me.project.dtos.response.order.OrderPaginationResponseDTO;
import me.project.dtos.response.page.PageResponse;
import me.project.service.order.IOrderService;
import me.project.service.order.part.IOrderPartService;
import com.stripe.exception.StripeException;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/orders")
@AllArgsConstructor
public class OrderController {
    private final IOrderService orderService;
    private final IOrderPartService orderPartService;

    @GetMapping("{orderId}/information")
    public OrderInformationDTO getOrderInformation(@PathVariable UUID orderId) {
        return OrderInformationDTO.convertFromEntity(orderService.getById(orderId));
    }

    @GetMapping("{orderId}/invoice")
    public OrderInvoiceDTO getOrderInvoice(@PathVariable UUID orderId) {
        return OrderInvoiceDTO.convertFromEntity(orderService.getById(orderId));
    }

    @GetMapping()
    public PageResponse<OrderPaginationResponseDTO> getOrders(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX") LocalDateTime orderDateFrom,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX") LocalDateTime orderDateTo,
            @RequestParam(required = false, defaultValue = "") UUID orderStatusId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageLimit,
            @RequestParam(required = false) String sortDir,
            @RequestParam(required = false, defaultValue = "createdOn") String sortBy,
            @RequestParam(required = false) String phrase,
            @RequestParam(required = false) UUID userId
    ) {
        return orderService.getOrders(
                new PageRequestDTO(page, pageLimit, sortDir, sortBy),
                phrase,
                orderDateFrom,
                orderDateTo,
                orderStatusId,
                userId);
    }

    @PostMapping("{orderId}/create-payment-intent")
    public OrderPaymentDTO createPaymentIntent(@PathVariable UUID orderId) throws StripeException {
        return orderService.createPaymentIntent(orderId);
    }

    @PostMapping("{orderId}/complete-payment")
    public void completePayment(@PathVariable UUID orderId) {
        orderService.completePayment(orderId);
    }

    @PostMapping()
    public UUID createOrder(@RequestBody OrderCreateRequestDTO request) {
        return orderService.createOrder(request);
    }

    @PostMapping("{orderId}/order-parts")
    public UUID createOrderPart(@PathVariable UUID orderId, @RequestBody OrderPartCreateDTO request) {
        return orderPartService.createOrderPart(orderId, request);
    }

    @PostMapping("{orderId}/order-services")
    public UUID createOrderService(@PathVariable UUID orderId, @RequestBody OrderServiceCreateRequestDTO request) {
        return orderService.addOrderServiceToOrder(orderId, request);
    }

    @PostMapping("{orderId}/order-parts/{orderPartId}")
    public void addOrderPartToOrder(@PathVariable UUID orderId, @PathVariable UUID orderPartId) {
        orderService.addOrderPartToOrder(orderId, orderPartId);
    }

    @PutMapping("{orderId}/order-parts/{orderPartId}")
    public void updateOrderPartOfOrder(@PathVariable UUID orderId, @PathVariable UUID orderPartId, @RequestBody OrderPartUpdateRequestDTO request) {
        orderService.updateOrdersOrderPart(orderId, orderPartId, request);
    }

    @PatchMapping("{orderId}/order-status/{orderStatusId}")
    public void updateOrderStatus(@PathVariable UUID orderId,@PathVariable UUID orderStatusId){
        orderService.updateOrderService(orderId, orderStatusId);
    }

    @PatchMapping("{orderId}/order-note")
    public void updateOrderNote(@PathVariable UUID orderId,@RequestBody String note){
        orderService.updateOrderNote(orderId, note);
    }

    @DeleteMapping("{orderId}/order-parts/{orderPartId}")
    public void deleteOrderPartOfOrder(@PathVariable UUID orderId, @PathVariable UUID orderPartId) {
        orderService.deleteOrdersOrderPart(orderId, orderPartId);
    }

    @DeleteMapping("{orderId}/order-services/{orderServiceId}")
    public void deleteOrderService(@PathVariable UUID orderId, @PathVariable UUID orderServiceId) {
        orderService.deleteOrdersOrderService(orderId, orderServiceId);
    }

}
