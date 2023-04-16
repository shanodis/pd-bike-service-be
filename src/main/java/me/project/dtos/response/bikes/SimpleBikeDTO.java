package me.project.dtos.response.bikes;

import me.project.entitiy.Bike;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
public class SimpleBikeDTO implements Serializable {
    private final UUID bikeId;
    private final String bikeName;
    private final String bikeMake;
    private final String bikeModel;
    private final String serialNumber;
    private final Integer yearOfProduction;

    public static SimpleBikeDTO convertFromEntity(Bike bike) {
        return new SimpleBikeDTO(
                bike.getBikeId(),
                bike.getBikeName(),
                bike.getBikeMake(),
                bike.getBikeModel(),
                bike.getSerialNumber(),
                bike.getYearOfProduction()
        );
    }

}
