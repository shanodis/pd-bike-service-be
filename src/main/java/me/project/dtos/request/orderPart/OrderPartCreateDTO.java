package me.project.dtos.request.orderPart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderPartCreateDTO implements Serializable {
    @NotNull
    private String orderCode;

    @NotNull
    private String orderName;

    @NotNull
    private BigDecimal orderPrice;
}
