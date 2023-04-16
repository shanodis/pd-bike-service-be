package me.project.dtos.response.address;

import me.project.dtos.response.country.CountryWithoutAddressesDTO;
import me.project.entitiy.Address;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
public class AddressDTO implements Serializable {
    private final UUID addressId;
    private final CountryWithoutAddressesDTO country;
    private final String streetName;
    private final String postCode;
    private final String city;

    public static AddressDTO convertFromEntity(Address address){
        return new AddressDTO(
                address.getAddressId(),
                CountryWithoutAddressesDTO.convertFromCountry(address.getCountry()),
                address.getStreetName(),
                address.getPostCode(),
                address.getCity()
        );
    }
}
