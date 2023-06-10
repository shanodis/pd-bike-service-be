package me.project.dtos.response.order;

import me.project.auth.enums.AppUserRole;
import me.project.entitiy.Bike;
import me.project.entitiy.Order;
import me.project.entitiy.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("OrderInformationDTO should")
class OrderInformationDTOTest {

    @Test
    @DisplayName("Should convert Order entity to OrderInformationDTO correctly")
    void convertFromEntityToOrderInformationDTO() {
        User user = new User();

        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUserId(UUID.randomUUID());

        Bike bike = new Bike(
                UUID.randomUUID(),
                user,
                "BikeName",
                "BikeMake",
                "BikeModel",
                "SerialNumber",
                2021,
                null,
                null
        );

        Order order = new Order(
                "note",
                LocalDateTime.now(),
                bike,
                user,
                null
        );

        OrderInformationDTO orderInformationDTO = OrderInformationDTO.convertFromEntity(order);

        assertNotNull(orderInformationDTO);
        assertEquals(user.getUserId(), orderInformationDTO.getUserId());
        assertEquals(user.getFirstName(), orderInformationDTO.getFirstName());
        assertEquals(user.getLastName(), orderInformationDTO.getLastName());
        assertEquals(bike.getBikeId(), orderInformationDTO.getBikeId());
        assertEquals(bike.getBikeName(), orderInformationDTO.getBikeName());
        assertEquals(order.getNote(), orderInformationDTO.getNote());
    }

}