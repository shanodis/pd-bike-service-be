package me.project.controller;

import me.project.dtos.response.country.CountryWithoutAddressesDTO;
import me.project.entitiy.Country;
import me.project.service.country.ICountryService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/countries")
@AllArgsConstructor
public class CountryController {
    private final ICountryService countryService;

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_CLIENT"})
    @GetMapping("{CountryId}")
    public Country getCountryById(@PathVariable UUID CountryId) {
        return countryService.getCountryById(CountryId);
    }

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_CLIENT"})
    @GetMapping
    public List<Country> getAllCountries() {
        return countryService.getAllCountries();
    }

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_CLIENT"})
    @GetMapping("countries")
    public List<CountryWithoutAddressesDTO> getAllCountriesWithoutAddresses() {
        return countryService.getAllCountriesWithoutAddresses();
    }

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE"})
    @PostMapping
    public CountryWithoutAddressesDTO createCountryIfNotExists(@RequestBody String CountryName) {
        return countryService.createCountryIfNotExists(CountryName);
    }

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE"})
    @PutMapping("{CountryId}")
    public void updateCountry(@PathVariable UUID CountryId,@RequestBody String NewCountryName) {
        countryService.updateCountry(CountryId, NewCountryName);
    }

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE"})
    @DeleteMapping("{CountryId}")
    public void deleteCountryById(@PathVariable UUID CountryId) {
        countryService.deleteCountryById(CountryId);
    }
}
