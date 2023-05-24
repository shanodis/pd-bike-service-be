package me.project.dtos.request;

import me.project.auth.User;
import me.project.entitiy.Bike;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BikeCreateDTO {
    @NotNull
    private UUID UserId;

    @NotNull
    private String BikeName;

    @NotNull
    private String BikeMake;

    @NotNull
    private String BikeModel;

    public Bike convertToBike(User user) {
        Bike bike = new Bike();

        bike.setUser(user);
        bike.setBikeName(BikeName);
        bike.setBikeMake(BikeMake);
        bike.setBikeModel(BikeModel);

        return bike;
    }
}
