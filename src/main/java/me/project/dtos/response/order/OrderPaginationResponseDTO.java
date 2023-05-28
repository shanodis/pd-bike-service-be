package me.project.dtos.response.order;

import me.project.entitiy.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class OrderPaginationResponseDTO implements Serializable {

    private final UUID orderId;

    private final String bikeName;

    private final String bikeModel;

    private final LocalDateTime createdOn;

    private final UUID userId;

    public static OrderPaginationResponseDTO convertFromEntity(Order order) {
        return new OrderPaginationResponseDTO(
                order.getOrderId(),
                order.getBike().getBikeName(),
                order.getBike().getBikeModel(),
                order.getCreatedOn(),
                order.getUser().getUserId()
        );
    }
}