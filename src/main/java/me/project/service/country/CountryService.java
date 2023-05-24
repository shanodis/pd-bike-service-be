package me.project.service.country;

import me.project.dtos.response.country.CountryWithoutAddressesDTO;
import me.project.entitiy.Country;
import me.project.repository.CountryRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CountryService implements ICountryService {
    private final CountryRepository countryRepository;

    private String NOT_FOUND(UUID CountryId) {
        return "Country with id" + CountryId + " doesn't exists in database";
    }

    public Country getCountryById(UUID CountryId) {
        return countryRepository.findById(CountryId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NOT_FOUND(CountryId))
        );
    }

    public List<Country> getAllCountries() {
        return countryRepository.findAll();
    }

    public List<CountryWithoutAddressesDTO> getAllCountriesWithoutAddresses() {
        List<CountryWithoutAddressesDTO> countries = new ArrayList<>();

        getAllCountries().forEach(country ->
                countries.add(CountryWithoutAddressesDTO.convertFromCountry(country))
        );

        return countries;
    }

    public CountryWithoutAddressesDTO createCountryIfNotExists(String CountryName) {

        Country country = new Country(CountryName);

        if (countryRepository.existsByCountryName(CountryName))
            return CountryWithoutAddressesDTO.convertFromCountry(countryRepository.getCountryByCountryName(CountryName));

        countryRepository.save(country);

        return CountryWithoutAddressesDTO.convertFromCountry(country);
    }

    public void updateCountry(UUID CountryId, String NewCountryName) {

        Country country = countryRepository.findById(CountryId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NOT_FOUND(CountryId))
        );

        country.setCountryName(NewCountryName);

        countryRepository.save(country);

    }

    public void deleteCountryById(UUID CountryId) {
        if (!countryRepository.existsById(CountryId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, NOT_FOUND(CountryId));

        countryRepository.deleteById(CountryId);
    }
}
