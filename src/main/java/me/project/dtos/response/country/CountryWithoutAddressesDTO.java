package me.project.dtos.response.country;

import me.project.entitiy.Country;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CountryWithoutAddressesDTO implements Serializable {
    private UUID countryId;
    private String countryName;

    public static CountryWithoutAddressesDTO convertFromCountry(Country country){
        return new CountryWithoutAddressesDTO(country.getCountryId(), country.getCountryName());
    }
}
