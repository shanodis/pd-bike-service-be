package me.project.dtos.response.services;

import me.project.entitiy.Service;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ServiceDTO implements Serializable {
    private final UUID serviceId;
    private final String serviceName;
    private final BigDecimal servicePrice;

    public static ServiceDTO convertFromEntity(Service service) {
        return new ServiceDTO(
                service.getServiceId(),
                service.getServiceName(),
                service.getServicePrice()
        );
    }
}
