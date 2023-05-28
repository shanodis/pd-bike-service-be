package me.project.dtos.request.orderService;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderServiceCreateRequestDTO {

    private UUID serviceId;
    private String serviceName;
    private BigDecimal servicePrice;
}
