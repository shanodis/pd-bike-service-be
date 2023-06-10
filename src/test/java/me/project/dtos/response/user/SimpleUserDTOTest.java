package me.project.dtos.response.user;

import me.project.auth.enums.AppUserRole;
import me.project.dtos.response.address.AddressDTO;
import me.project.dtos.response.company.CompanyDTO;
import me.project.entitiy.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("SimpleUserDTO should")
class SimpleUserDTOTest {

    @Test
    @DisplayName("Should convert User entity to SimpleUserDTO when address is null")
    void convertFromEntityWhenAddressIsNull() {
        UUID userId = UUID.randomUUID();
        String email = "test@example.com";
        String firstName = "John";
        String lastName = "Doe";
        String phoneNumberPrefix = "+1";
        String phoneNumber = "1234567890";
        AppUserRole appUserRole = AppUserRole.CLIENT;

        User user = new User();
        user.setUserId(userId);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhoneNumberPrefix(phoneNumberPrefix);
        user.setPhoneNumber(phoneNumber);
        user.setAppUserRole(appUserRole);

        SimpleUserDTO simpleUserDTO = SimpleUserDTO.convertFromEntity(user);

        assertEquals(userId, simpleUserDTO.getUserId());
        assertEquals(email, simpleUserDTO.getEmail());
        assertEquals(firstName, simpleUserDTO.getFirstName());
        assertEquals(lastName, simpleUserDTO.getLastName());
        assertEquals(phoneNumberPrefix, simpleUserDTO.getPhoneNumberPrefix());
        assertEquals(phoneNumber, simpleUserDTO.getPhoneNumber());
        assertNull(simpleUserDTO.getAddress());
        assertNull(simpleUserDTO.getCompany());
        assertEquals(appUserRole, simpleUserDTO.getAppUserRole());
    }

    @Test
    @DisplayName("Should convert User entity to SimpleUserDTO when company is null")
    void convertFromEntityWhenCompanyIsNull() {
        UUID userId = UUID.randomUUID();
        String email = "test@example.com";
        String firstName = "John";
        String lastName = "Doe";
        String phoneNumberPrefix = "+1";
        String phoneNumber = "1234567890";
        AppUserRole appUserRole = AppUserRole.CLIENT;

        User user = new User();
        user.setUserId(userId);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhoneNumberPrefix(phoneNumberPrefix);
        user.setPhoneNumber(phoneNumber);
        user.setAppUserRole(appUserRole);

        SimpleUserDTO simpleUserDTO = SimpleUserDTO.convertFromEntity(user);

        assertEquals(userId, simpleUserDTO.getUserId());
        assertEquals(email, simpleUserDTO.getEmail());
        assertEquals(firstName, simpleUserDTO.getFirstName());
        assertEquals(lastName, simpleUserDTO.getLastName());
        assertEquals(phoneNumberPrefix, simpleUserDTO.getPhoneNumberPrefix());
        assertEquals(phoneNumber, simpleUserDTO.getPhoneNumber());
        assertNull(simpleUserDTO.getCompany());
        assertNull(simpleUserDTO.getAddress());
        assertEquals(appUserRole, simpleUserDTO.getAppUserRole());
    }

    @Test
    @DisplayName("Should convert User entity to SimpleUserDTO when both company and address are null")
    void convertFromEntityWhenBothCompanyAndAddressAreNull() {
        UUID userId = UUID.randomUUID();
        String email = "test@example.com";
        String firstName = "John";
        String lastName = "Doe";
        String phoneNumberPrefix = "+1";
        String phoneNumber = "1234567890";
        AppUserRole appUserRole = AppUserRole.CLIENT;

        User user = new User();
        user.setUserId(userId);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhoneNumberPrefix(phoneNumberPrefix);
        user.setPhoneNumber(phoneNumber);
        user.setAppUserRole(appUserRole);

        SimpleUserDTO simpleUserDTO = SimpleUserDTO.convertFromEntity(user);

        assertEquals(userId, simpleUserDTO.getUserId());
        assertEquals(email, simpleUserDTO.getEmail());
        assertEquals(firstName, simpleUserDTO.getFirstName());
        assertEquals(lastName, simpleUserDTO.getLastName());
        assertEquals(phoneNumberPrefix, simpleUserDTO.getPhoneNumberPrefix());
        assertEquals(phoneNumber, simpleUserDTO.getPhoneNumber());
        assertEquals(appUserRole, simpleUserDTO.getAppUserRole());
        assertNull(simpleUserDTO.getCompany());
        assertNull(simpleUserDTO.getAddress());
    }

}