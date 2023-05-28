package me.project.dtos.response.bike;

import me.project.entitiy.Bike;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BikeResponseDTO {
    @NotNull
    private String BikeName;

    @NotNull
    private String BikeMake;

    @NotNull
    private String BikeModel;

    private String SerialNumber;

    private Integer YearOfProduction;

    public static BikeResponseDTO convertFromBike(Bike bike) {
        return new BikeResponseDTO(
                bike.getBikeName(),
                bike.getBikeMake(),
                bike.getBikeModel(),
                bike.getSerialNumber(),
                bike.getYearOfProduction()
        );
    }
}
