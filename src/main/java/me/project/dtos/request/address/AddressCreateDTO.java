package me.project.dtos.request.address;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.project.entitiy.Address;
import me.project.entitiy.Country;
import me.project.entitiy.User;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressCreateDTO implements Serializable {
    @NotNull
    private String streetName;

    @NotNull
    private String postCode;

    @NotNull
    private String city;

    @NotNull
    private Country country;

    @NotNull
    private User user;

    public Address convertToEntity() {
        Address address = new Address();
        address.setStreetName(streetName);
        address.setCity(city);
        address.setCountry(country);
        address.setPostCode(postCode);
        address.setUser(user);
        return address;
    }
}
