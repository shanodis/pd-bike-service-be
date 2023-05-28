package me.project.controller;


import me.project.dtos.request.PageRequestDTO;
import me.project.dtos.request.order.OrderCreateRequestDTO;
import me.project.dtos.request.orderPart.OrderPartCreateDTO;
import me.project.dtos.response.order.OrderInformationDTO;
import me.project.dtos.response.order.OrderInvoiceDTO;
import me.project.dtos.response.order.OrderPaginationResponseDTO;
import me.project.dtos.response.page.PageResponse;
import me.project.service.order.IOrderService;
import me.project.service.order.part.IOrderPartService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageLimit,
            @RequestParam(required = false) String sortDir,
            @RequestParam(required = false, defaultValue = "createdOn") String sortBy,
            @RequestParam(required = false) String phrase
    ) {
        return orderService.getOrders(new PageRequestDTO(page, pageLimit, sortDir, sortBy), phrase);
    }

    @PostMapping()
    public UUID createOrder(@RequestBody OrderCreateRequestDTO request) {
        return orderService.createOrder(request);
    }

    @PostMapping("{orderId}/order-parts")
    public UUID createOrderPart(@PathVariable UUID orderId, OrderPartCreateDTO request) {
        return orderPartService.createOrderPart(orderId, request);
    }

    @PostMapping("{orderId}/order-parts/{orderPartId}")
    public void addOrderPartToOrder(@PathVariable UUID orderId, @PathVariable UUID orderPartId) {
        orderService.addOrderPartToOrder(orderId, orderPartId);
    }

    @PutMapping("{orderId}/order-parts/{orderPartId}/{newOrderPartId}")
    public void updateOrderPartOfOrder(@PathVariable UUID orderId, @PathVariable UUID orderPartId, @PathVariable UUID newOrderPartId) {
        orderService.updateOrdersOrderPart(orderId, orderPartId, newOrderPartId);
    }

    @DeleteMapping("{orderId}/order-parts/{orderPartId}")
    public void deleteOrderPartOfOrder(@PathVariable UUID orderId, @PathVariable UUID orderPartId) {
        orderService.deleteOrdersOrderPart(orderId, orderPartId);
    }

}
