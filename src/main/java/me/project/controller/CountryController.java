package me.project.controller;

import me.project.dtos.response.country.CountryWithoutAddressesDTO;
import me.project.entitiy.Country;
import me.project.service.country.ICountryService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/countries")
@AllArgsConstructor
public class CountryController {
    private final ICountryService countryService;

    @GetMapping("{CountryId}")
    public Country getCountryById(@PathVariable UUID CountryId) {
        return countryService.getCountryById(CountryId);
    }

    @GetMapping
    public List<Country> getAllCountries() {
        return countryService.getAllCountries();
    }

    @GetMapping("without-addresses")
    public List<CountryWithoutAddressesDTO> getAllCountriesWithoutAddresses() {
        return countryService.getAllCountriesWithoutAddresses();
    }

    @PostMapping("{CountryName}")
    public CountryWithoutAddressesDTO createCountryIfNotExists(@PathVariable String CountryName) {
        return countryService.createCountryIfNotExists(CountryName);
    }

    @PutMapping("{CountryId}&{NewCountryName}")
    public void updateCountry(@PathVariable UUID CountryId,@PathVariable String NewCountryName) {
        countryService.updateCountry(CountryId, NewCountryName);
    }

    @DeleteMapping("{CountryId}")
    public void deleteCountryById(@PathVariable UUID CountryId) {
        countryService.deleteCountryById(CountryId);
    }
}
