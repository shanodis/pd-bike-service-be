package me.project.dtos.response.order;

import me.project.entitiy.OrderService;
import me.project.entitiy.Service;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
public class OrderServiceDTO implements Serializable {
    private final UUID orderServiceId;
    private final UUID serviceId;
    private final String serviceName;
    private final BigDecimal orderPrice;

    public static OrderServiceDTO convertFromEntity(OrderService orderService) {

        Service service = orderService.getService();

        return new OrderServiceDTO(
                orderService.getOrderServiceId(),
                service.getServiceId(),
                service.getServiceName(),
                service.getServicePrice()
        );
    }
}
