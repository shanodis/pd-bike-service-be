package me.project.dtos.request.service;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class CreateServiceDTO implements Serializable {

    @NotNull
    private String serviceName;

    @NotNull
    private BigDecimal servicePrice;
}
