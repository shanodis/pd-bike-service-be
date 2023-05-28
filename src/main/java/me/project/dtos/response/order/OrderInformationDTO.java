package me.project.dtos.response.order;

import me.project.entitiy.Bike;
import me.project.entitiy.Order;
import me.project.entitiy.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
public class OrderInformationDTO implements Serializable {
    private final UUID userId;
    private final String firstName;
    private final String lastName;
    private final UUID bikeId;
    private final String bikeName;
    private final String note;

    public static OrderInformationDTO convertFromEntity(Order order) {
        User user = order.getUser();
        Bike bike = order.getBike();

        return new OrderInformationDTO(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                bike.getBikeId(),
                bike.getBikeName(),
                order.getNote()
        );
    }
}
