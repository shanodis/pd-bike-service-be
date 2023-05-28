package me.project.dtos.response.order;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class OrderPaymentDTO implements Serializable {
    private String clientSecret;
    private BigDecimal totalPrice;
}
