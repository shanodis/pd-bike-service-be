package me.project.dtos.response.user;

import me.project.auth.enums.AppUserRole;
import me.project.entitiy.Address;
import me.project.entitiy.Company;
import me.project.entitiy.Country;
import me.project.entitiy.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("CustomerDetailsDTO should")
class CustomerDetailsDTOTest {

    @Test
    @DisplayName("Should convert User entity to CustomerDetailsDTO when company and address are not present")
    void convertFromEntityWhenCompanyAndAddressNotPresent() {
        UUID userId = UUID.randomUUID();
        String firstName = "John";
        String lastName = "Doe";
        String phoneNumberPrefix = "+1";
        String phoneNumber = "1234567890";
        String note = "Test note";
        String email = "john.doe@example.com";
        Boolean isUsing2FA = true;

        User user = new User();
        user.setUserId(userId);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhoneNumberPrefix(phoneNumberPrefix);
        user.setPhoneNumber(phoneNumber);
        user.setEmail(email);
        user.setNote(note);
        user.setIsUsing2FA(isUsing2FA);
        user.setAppUserRole(AppUserRole.CLIENT);

        CustomerDetailsDTO customerDetailsDTO = CustomerDetailsDTO.convertFromEntity(user);

        assertEquals(userId, customerDetailsDTO.getUserId());
        assertEquals(firstName, customerDetailsDTO.getFirstName());
        assertEquals(lastName, customerDetailsDTO.getLastName());
        assertEquals(phoneNumberPrefix, customerDetailsDTO.getPhoneNumberPrefix());
        assertEquals(phoneNumber, customerDetailsDTO.getPhoneNumber());
        assertEquals(note, customerDetailsDTO.getNote());
        assertEquals(email, customerDetailsDTO.getEmail());
        assertEquals(isUsing2FA, customerDetailsDTO.getIsUsing2FA());
        assertNull(customerDetailsDTO.getCompanyId());
        assertNull(customerDetailsDTO.getCompanyName());
        assertNull(customerDetailsDTO.getTaxNumber());
        assertNull(customerDetailsDTO.getAddressId());
        assertNull(customerDetailsDTO.getStreetName());
        assertNull(customerDetailsDTO.getPostCode());
        assertNull(customerDetailsDTO.getCity());
        assertNull(customerDetailsDTO.getCountryId());
        assertNull(customerDetailsDTO.getCountryName());
    }

    @Test
    @DisplayName("Should convert User entity to CustomerDetailsDTO when company is present and address is not present")
    void convertFromEntityWhenCompanyPresentAndAddressNotPresent() {
        UUID userId = UUID.randomUUID();
        String firstName = "John";
        String lastName = "Doe";
        String phoneNumberPrefix = "+1";
        String phoneNumber = "1234567890";
        String note = "Test note";
        String email = "john.doe@example.com";
        Boolean isUsing2FA = true;

        UUID companyId = UUID.randomUUID();
        String companyName = "Test Company";
        String taxNumber = "1234567890";

        User user = new User();
        user.setUserId(userId);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhoneNumberPrefix(phoneNumberPrefix);
        user.setPhoneNumber(phoneNumber);
        user.setEmail(email);
        user.setNote(note);
        user.setIsUsing2FA(isUsing2FA);
        user.setAppUserRole(AppUserRole.CLIENT);

        Company company = new Company(companyName, taxNumber, user);
        company.setCompanyId(companyId);
        user.setCompany(company);

        CustomerDetailsDTO customerDetailsDTO = CustomerDetailsDTO.convertFromEntity(user);

        assertEquals(userId, customerDetailsDTO.getUserId());
        assertEquals(firstName, customerDetailsDTO.getFirstName());
        assertEquals(lastName, customerDetailsDTO.getLastName());
        assertEquals(phoneNumberPrefix, customerDetailsDTO.getPhoneNumberPrefix());
        assertEquals(phoneNumber, customerDetailsDTO.getPhoneNumber());
        assertEquals(note, customerDetailsDTO.getNote());
        assertEquals(email, customerDetailsDTO.getEmail());
        assertEquals(isUsing2FA, customerDetailsDTO.getIsUsing2FA());
        assertEquals(companyId, customerDetailsDTO.getCompanyId());
        assertEquals(companyName, customerDetailsDTO.getCompanyName());
        assertEquals(taxNumber, customerDetailsDTO.getTaxNumber());
        assertNull(customerDetailsDTO.getAddressId());
        assertNull(customerDetailsDTO.getStreetName());
        assertNull(customerDetailsDTO.getPostCode());
        assertNull(customerDetailsDTO.getCity());
        assertNull(customerDetailsDTO.getCountryId());
        assertNull(customerDetailsDTO.getCountryName());
    }

    @Test
    @DisplayName("Should convert User entity to CustomerDetailsDTO when company and address are present")
    void convertFromEntityWhenCompanyAndAddressPresent() {        // Create mock objects
        User user = mock(User.class);
        Company company = mock(Company.class);
        Address address = mock(Address.class);
        Country country = mock(Country.class);

        // Set up mock objects
        when(user.getUserId()).thenReturn(UUID.randomUUID());
        when(user.getFirstName()).thenReturn("John");
        when(user.getLastName()).thenReturn("Doe");
        when(user.getPhoneNumberPrefix()).thenReturn("+1");
        when(user.getPhoneNumber()).thenReturn("123456789");
        when(user.getNote()).thenReturn("Test note");
        when(user.getEmail()).thenReturn("john.doe@example.com");
        when(user.getIsUsing2FA()).thenReturn(true);

        when(company.getCompanyId()).thenReturn(UUID.randomUUID());
        when(company.getCompanyName()).thenReturn("Test Company");
        when(company.getTaxNumber()).thenReturn("1234567890");

        when(address.getAddressId()).thenReturn(UUID.randomUUID());
        when(address.getStreetName()).thenReturn("Test Street");
        when(address.getPostCode()).thenReturn("12345");
        when(address.getCity()).thenReturn("Test City");

        when(country.getCountryId()).thenReturn(UUID.randomUUID());
        when(country.getCountryName()).thenReturn("Test Country");

        when(user.getCompany()).thenReturn(company);
        when(user.getAddress()).thenReturn(address);
        when(address.getCountry()).thenReturn(country);

        // Call the method under test
        CustomerDetailsDTO customerDetailsDTO = CustomerDetailsDTO.convertFromEntity(user);

        // Verify the result
        assertNotNull(customerDetailsDTO);
        assertEquals(user.getUserId(), customerDetailsDTO.getUserId());
        assertEquals(user.getFirstName(), customerDetailsDTO.getFirstName());
        assertEquals(user.getLastName(), customerDetailsDTO.getLastName());
        assertEquals(user.getPhoneNumberPrefix(), customerDetailsDTO.getPhoneNumberPrefix());
        assertEquals(user.getPhoneNumber(), customerDetailsDTO.getPhoneNumber());
        assertEquals(user.getNote(), customerDetailsDTO.getNote());
        assertEquals(user.getEmail(), customerDetailsDTO.getEmail());
        assertEquals(user.getIsUsing2FA(), customerDetailsDTO.getIsUsing2FA());
        assertEquals(company.getCompanyId(), customerDetailsDTO.getCompanyId());
        assertEquals(company.getCompanyName(), customerDetailsDTO.getCompanyName());
        assertEquals(company.getTaxNumber(), customerDetailsDTO.getTaxNumber());
        assertEquals(address.getAddressId(), customerDetailsDTO.getAddressId());
        assertEquals(address.getStreetName(), customerDetailsDTO.getStreetName());
        assertEquals(address.getPostCode(), customerDetailsDTO.getPostCode());
        assertEquals(address.getCity(), customerDetailsDTO.getCity());
        assertEquals(country.getCountryId(), customerDetailsDTO.getCountryId());
        assertEquals(country.getCountryName(), customerDetailsDTO.getCountryName());
    }

}