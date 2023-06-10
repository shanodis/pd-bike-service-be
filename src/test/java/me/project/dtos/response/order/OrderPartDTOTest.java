package me.project.dtos.response.order;

import me.project.entitiy.OrderPart;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("OrderPartDTO")
class OrderPartDTOTest {

    @Test
    @DisplayName("Should convert OrderPart entity to OrderPartDTO")
    void convertFromEntityToOrderPartDTO() {
        OrderPart orderPart = new OrderPart(
                UUID.randomUUID(),
                null,
                "code",
                "name",
                BigDecimal.valueOf(100.0)
        );

        OrderPartDTO orderPartDTO = OrderPartDTO.convertFromEntity(orderPart);

        assertNotNull(orderPartDTO);
        assertEquals(orderPart.getOrderPartId(), orderPartDTO.getOrderPartId());
        assertEquals(orderPart.getOrderCode(), orderPartDTO.getOrderCode());
        assertEquals(orderPart.getOrderName(), orderPartDTO.getOrderName());
        assertEquals(orderPart.getOrderPrice(), orderPartDTO.getOrderPrice());
    }

}