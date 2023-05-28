package me.project.dtos.response.order;

import me.project.entitiy.OrderPart;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
public class OrderPartDTO implements Serializable {
    private final UUID orderPartId;
    private final String orderCode;
    private final String orderName;
    private final BigDecimal orderPrice;

    public static OrderPartDTO convertFromEntity(OrderPart part) {
        return new OrderPartDTO(
                part.getOrderPartId(),
                part.getOrderCode(),
                part.getOrderName(),
                part.getOrderPrice()
        );
    }
}
