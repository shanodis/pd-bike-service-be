package me.project.dtos.request.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

@Data
public class OrderCreateRequestDTO implements Serializable {
    @NotNull
    private UUID userId;

    @NotNull
    private String note;

    private UUID bikeId;

    private String bikeName;

    private String bikeMake;

    private String bikeModel;

    private String serialNumber;

    private int yearOfProduction;
}
