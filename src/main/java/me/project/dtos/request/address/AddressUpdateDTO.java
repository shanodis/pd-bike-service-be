package me.project.dtos.request.address;

import me.project.entitiy.Address;
import me.project.entitiy.Country;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressUpdateDTO implements Serializable {

    @NotNull
    private String streetName;

    @NotNull
    private String postCode;

    @NotNull
    private String city;

    @NotNull
    private Country country;

    public Address convertToEntity(Address address) {
        address.setStreetName(streetName);
        address.setCity(city);
        address.setCountry(country);
        address.setPostCode(postCode);
        return address;
    }
}
