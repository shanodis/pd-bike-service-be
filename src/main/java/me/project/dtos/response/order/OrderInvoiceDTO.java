package me.project.dtos.response.order;

import me.project.entitiy.Order;
import me.project.entitiy.OrderPart;
import me.project.entitiy.OrderService;
import me.project.entitiy.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class OrderInvoiceDTO implements Serializable {
    private final List<OrderPartDTO> orderParts;
    private final List<OrderServiceDTO> orderServices;
    private final String note;
    private final UUID orderStatusId;
    private final String orderStatusName;
    private final LocalDateTime createdOn;
    private final BigDecimal totalPrice;
    private final Boolean isPaid;

    public static OrderInvoiceDTO convertFromEntity(Order order) {

        OrderStatus orderStatus = order.getOrderStatus();

        List<OrderPart> orderParts = order.getOrderParts();

        List<OrderService> orderServices = order.getOrderServices();

        final BigDecimal[] totalPrice = {new BigDecimal(0)};

        orderParts.forEach(orderPart -> totalPrice[0] = totalPrice[0].add(orderPart.getOrderPrice()));

        orderServices.forEach(orderService -> totalPrice[0] = totalPrice[0].add(orderService.getService().getServicePrice()));

        return new OrderInvoiceDTO(

                orderParts
                        .stream()
                        .map(OrderPartDTO::convertFromEntity)
                        .collect(Collectors.toList()),

                orderServices
                        .stream()
                        .map(OrderServiceDTO::convertFromEntity)
                        .collect(Collectors.toList()),

                order.getNote(),

                orderStatus.getOrderStatusId(),

                orderStatus.getOrderStatusName(),

                order.getCreatedOn(),

                totalPrice[0],

                order.getIsPayed()
        );
    }
}
