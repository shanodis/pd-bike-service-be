package me.project.service.country;


import me.project.dtos.response.country.CountryWithoutAddressesDTO;
import me.project.entitiy.Country;

import java.util.List;
import java.util.UUID;

public interface ICountryService {

    Country getCountryById(UUID CountryId);

    List<Country> getAllCountries();

    List<CountryWithoutAddressesDTO> getAllCountriesWithoutAddresses();

    CountryWithoutAddressesDTO createCountryIfNotExists(String CountryName);

    void updateCountry(UUID CountryId, String NewCountryName );

    void deleteCountryById(UUID CountryId);
}
