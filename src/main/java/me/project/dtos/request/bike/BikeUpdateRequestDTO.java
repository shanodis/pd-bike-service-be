package me.project.dtos.request.bike;

import me.project.entitiy.Bike;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BikeUpdateRequestDTO {
    @NotNull
    private String BikeName;

    @NotNull
    private String BikeMake;

    @NotNull
    private String BikeModel;

    private String SerialNumber;

    private Integer YearOfProduction;

    public Bike convertToBike(Bike bike) {

        bike.setBikeName(BikeName);
        bike.setBikeMake(BikeMake);
        bike.setBikeModel(BikeModel);
        bike.setSerialNumber(SerialNumber);
        bike.setYearOfProduction(YearOfProduction);

        return bike;
    }
}
