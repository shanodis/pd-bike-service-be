package me.project.dtos.response.country;

import me.project.entitiy.Country;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("CountryWithoutAddressesDTO tests")
class CountryWithoutAddressesDTOTest {

    @Test
    @DisplayName("Should convert a Country object to a CountryWithoutAddressesDTO object")
    void convertFromCountryToCountryWithoutAddressesDTO() {        // Create a mock Country object
        Country country = new Country("Test Country");
        country.setCountryId(UUID.randomUUID());

        // Call the convertFromCountry method
        CountryWithoutAddressesDTO countryWithoutAddressesDTO = CountryWithoutAddressesDTO.convertFromCountry(country);

        // Assert that the converted object has the correct values
        assertEquals(country.getCountryId(), countryWithoutAddressesDTO.getCountryId());
        assertEquals(country.getCountryName(), countryWithoutAddressesDTO.getCountryName());
    }

}