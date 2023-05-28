package me.project.dtos.request.orderPart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderPartUpdateRequestDTO {

    private String orderCode;

    private String orderName;

    private BigDecimal orderPrice;

}
