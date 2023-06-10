package me.project.service.country;

import me.project.dtos.request.PageRequestDTO;
import me.project.dtos.response.country.CountryWithoutAddressesDTO;
import me.project.dtos.response.page.PageResponse;
import me.project.entitiy.Country;
import me.project.repository.CountryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CountryServiceTest {

    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private CountryService countryService;

    @Test
    @DisplayName("Should return the country when the country ID is found")
    void getCountryByIdWhenIdIsFound() {
        UUID countryId = UUID.randomUUID();
        Country country = new Country("Test Country");
        country.setCountryId(countryId);

        when(countryRepository.findById(countryId)).thenReturn(java.util.Optional.of(country));

        Country result = countryService.getCountryById(countryId);

        assertEquals(country, result);
        verify(countryRepository, times(1)).findById(countryId);
    }

    @Test
    @DisplayName(
            "Should throw a ResponseStatusException with HttpStatus.NOT_FOUND when the country ID is not found")
    void getCountryByIdWhenIdIsNotFoundThenThrowResponseStatusException() {
        UUID countryId = UUID.randomUUID();

        when(countryRepository.findById(countryId)).thenReturn(java.util.Optional.empty());

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> {
                            countryService.getCountryById(countryId);
                        });

        assertEquals(exception.getStatus(), HttpStatus.NOT_FOUND);
        assertEquals(
                exception.getReason(),
                "Country with id" + countryId + " doesn't exists in database");

        verify(countryRepository, times(1)).findById(countryId);
    }

    @Test
    @DisplayName("Should return all countries")
    void getAllCountries() {
        List<Country> countries = new ArrayList<>();
        countries.add(new Country(UUID.randomUUID(), "Country 1", null));
        countries.add(new Country(UUID.randomUUID(), "Country 2", null));
        countries.add(new Country(UUID.randomUUID(), "Country 3", null));
        // Mocking the behavior of the countryRepository
        when(countryRepository.findAll()).thenReturn(countries);

        List<CountryWithoutAddressesDTO> result = countryService.getAllCountriesWithoutAddresses();

        assertEquals(3, result.size());
        assertEquals("Country 1", result.get(0).getCountryName());
        assertEquals("Country 2", result.get(1).getCountryName());
        assertEquals("Country 3", result.get(2).getCountryName());

        // Verify that the countryRepository.findAll() method was called exactly once
        verify(countryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return a list of countries without addresses")
    void getAllCountriesWithoutAddresses() { // create a list of countries with addresses
        List<Country> countriesWithAddresses = new ArrayList<>();
        Country country1 = new Country(UUID.randomUUID(), "Country 1", new ArrayList<>());
        Country country2 = new Country(UUID.randomUUID(), "Country 2", new ArrayList<>());
        countriesWithAddresses.add(country1);
        countriesWithAddresses.add(country2);

        // create a list of countries without addresses
        List<CountryWithoutAddressesDTO> expectedCountriesWithoutAddresses = new ArrayList<>();
        CountryWithoutAddressesDTO countryWithoutAddressesDTO1 =
                new CountryWithoutAddressesDTO(country1.getCountryId(), country1.getCountryName());
        CountryWithoutAddressesDTO countryWithoutAddressesDTO2 =
                new CountryWithoutAddressesDTO(country2.getCountryId(), country2.getCountryName());
        expectedCountriesWithoutAddresses.add(countryWithoutAddressesDTO1);
        expectedCountriesWithoutAddresses.add(countryWithoutAddressesDTO2);

        // mock the behavior of countryRepository.findAll() method
        when(countryRepository.findAll()).thenReturn(countriesWithAddresses);

        // call the method under test
        List<CountryWithoutAddressesDTO> actualCountriesWithoutAddresses =
                countryService.getAllCountriesWithoutAddresses();

        // verify the result
        assertEquals(expectedCountriesWithoutAddresses, actualCountriesWithoutAddresses);

        // verify that countryRepository.findAll() method is called exactly once
        verify(countryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return an empty page when there are no countries in the database")
    void getAllCountriesWhenNoCountriesInDatabase() {
        PageRequestDTO pageRequestDTO = new PageRequestDTO(1, 10, "asc", "countryName");

        when(countryRepository.findAll(pageRequestDTO.getRequest(Country.class)))
                .thenReturn(Page.empty());

        PageResponse<CountryWithoutAddressesDTO> result =
                countryService.getAllCountries(pageRequestDTO);

        assertEquals(0, result.getContent().size());
        assertEquals(1, result.getTotalPages());
        assertEquals(1, result.getCurrentPage());
    }

    @Test
    @DisplayName(
            "Should return a page with less items than requested when there are not enough countries in the database")
    void getAllCountriesWhenNotEnoughCountriesInDatabase() {
        PageRequestDTO pageRequestDTO = new PageRequestDTO(1, 10, "asc", "countryName");
        when(countryRepository.findAll(pageRequestDTO.getRequest(Country.class)))
                .thenReturn(Page.empty());

        PageResponse<CountryWithoutAddressesDTO> result =
                countryService.getAllCountries(pageRequestDTO);

        assertEquals(0, result.getContent().size());
        assertEquals(1, result.getCurrentPage());
        assertEquals(1, result.getTotalPages());
        verify(countryRepository, times(1)).findAll(pageRequestDTO.getRequest(Country.class));
    }

    @Test
    @DisplayName(
            "Should return a page of countries without addresses when given a valid PageRequestDTO")
    void getAllCountriesWithValidPageRequestDTO() {
        PageRequestDTO pageRequestDTO = new PageRequestDTO(1, 10, "asc", "countryName");
        List<Country> countries = new ArrayList<>();
        countries.add(new Country(UUID.randomUUID(), "Country 1", new ArrayList<>()));
        countries.add(new Country(UUID.randomUUID(), "Country 2", new ArrayList<>()));
        Page<Country> countryPage = new PageImpl<>(countries);
        when(countryRepository.findAll(pageRequestDTO.getRequest(Country.class)))
                .thenReturn(countryPage);

        PageResponse<CountryWithoutAddressesDTO> result =
                countryService.getAllCountries(pageRequestDTO);

        assertEquals(countryPage.getTotalPages(), result.getTotalPages());
        assertEquals(countryPage.getNumber() + 1, result.getCurrentPage());
        assertEquals(countryPage.getContent().size(), result.getContent().size());
        assertEquals(
                CountryWithoutAddressesDTO.convertFromCountry(countryPage.getContent().get(0)),
                result.getContent().get(0));
        assertEquals(
                CountryWithoutAddressesDTO.convertFromCountry(countryPage.getContent().get(1)),
                result.getContent().get(1));
        verify(countryRepository, times(1)).findAll(pageRequestDTO.getRequest(Country.class));
    }

    @Test
    @DisplayName("Should return existing country when the country name already exists")
    void createCountryIfNotExistsWhenCountryNameExists() {
        String countryName = "USA";
        UUID countryId = UUID.randomUUID();
        Country country = new Country(countryName);
        country.setCountryId(countryId);

        when(countryRepository.existsByCountryName(countryName)).thenReturn(true);
        when(countryRepository.getCountryByCountryName(countryName)).thenReturn(country);

        CountryWithoutAddressesDTO result = countryService.createCountryIfNotExists(countryName);

        verify(countryRepository, times(0)).save(any(Country.class));
        assertEquals(countryId, result.getCountryId());
        assertEquals(countryName, result.getCountryName());
    }

    @Test
    @DisplayName("Should create and return a new country when the country name does not exist")
    void createCountryIfNotExistsWhenCountryNameDoesNotExist() {
        String countryName = "Test Country";
        Country country = new Country(countryName);
        Country savedCountry = new Country(countryName);
        CountryWithoutAddressesDTO expectedCountryDTO =
                new CountryWithoutAddressesDTO(
                        savedCountry.getCountryId(), savedCountry.getCountryName());

        when(countryRepository.existsByCountryName(countryName)).thenReturn(false);
        when(countryRepository.save(any(Country.class))).thenReturn(savedCountry);

        CountryWithoutAddressesDTO actualCountryDTO =
                countryService.createCountryIfNotExists(countryName);

        assertEquals(expectedCountryDTO, actualCountryDTO);
        verify(countryRepository, times(1)).existsByCountryName(countryName);
        verify(countryRepository, times(1)).save(any(Country.class));
    }

    @Test
    @DisplayName("Should update the country name when the country id is valid")
    void updateCountryWhenCountryIdIsValid() {
        UUID countryId = UUID.randomUUID();
        String newCountryName = "New Country Name";
        Country country = new Country("Old Country Name");
        country.setCountryId(countryId);

        when(countryRepository.findById(countryId)).thenReturn(java.util.Optional.of(country));
        when(countryRepository.save(country)).thenReturn(country);

        countryService.updateCountry(countryId, newCountryName);

        verify(countryRepository, times(1)).findById(countryId);
        verify(countryRepository, times(1)).save(country);
        assert (country.getCountryName().equals(newCountryName));
    }

    @Test
    @DisplayName("Should throw a not found exception when the country id is not found")
    void updateCountryWhenCountryIdNotFoundThenThrowException() {
        UUID countryId = UUID.randomUUID();
        String newCountryName = "New Country Name";
        when(countryRepository.findById(countryId)).thenReturn(java.util.Optional.empty());

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> {
                            countryService.updateCountry(countryId, newCountryName);
                        });

        verify(countryRepository, times(1)).findById(countryId);
        verify(countryRepository, times(0)).save(any(Country.class));
        assertEquals(
                "404 NOT_FOUND \"Country with id" + countryId + " doesn't exists in database\"",
                exception.getMessage());
    }

    @Test
    @DisplayName("Should delete the country when the country id exists")
    void deleteCountryByIdWhenCountryIdExists() {
        UUID countryId = UUID.randomUUID();
        Country country = new Country("Test Country");
        country.setCountryId(countryId);

        when(countryRepository.existsById(countryId)).thenReturn(true);

        countryService.deleteCountryById(countryId);

        verify(countryRepository, times(1)).deleteById(countryId);
    }

    @Test
    @DisplayName("Should throw an exception when the country id does not exist")
    void deleteCountryByIdWhenCountryIdDoesNotExistThenThrowException() {
        UUID countryId = UUID.randomUUID();

        when(countryRepository.existsById(countryId)).thenReturn(false);

        assertThrows(
                ResponseStatusException.class,
                () -> {
                    countryService.deleteCountryById(countryId);
                });

        verify(countryRepository, times(1)).existsById(countryId);
        verify(countryRepository, never()).deleteById(countryId);
    }
}