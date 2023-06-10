package me.project.dtos.response.services;

import me.project.entitiy.Service;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("ServiceDTO")
class ServiceDTOTest {

    @Test
    @DisplayName("Should convert Service entity to ServiceDTO")
    void convertFromEntityToServiceDTO() {
        Service service = new Service(
                UUID.randomUUID(),
                "Test Service",
                new BigDecimal("10.00"),
                new ArrayList<>()
        );

        ServiceDTO serviceDTO = ServiceDTO.convertFromEntity(service);

        assertNotNull(serviceDTO);
        assertEquals(service.getServiceId(), serviceDTO.getServiceId());
        assertEquals(service.getServiceName(), serviceDTO.getServiceName());
        assertEquals(service.getServicePrice(), serviceDTO.getServicePrice());
    }

}