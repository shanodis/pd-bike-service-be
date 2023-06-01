package me.project.entitiy;

import me.project.auth.enums.AppUserRole;
import me.project.dtos.request.user.UserCreateDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User class tests")
class UserTest {

    @Test
    @DisplayName(
            "Should create a user with the given user credentials, locked status, and enabled status")
    void createUserWithUserCredentialsLockedAndEnabled() {
        UserCreateDTO userCreateDTO =
                new UserCreateDTO(
                        "test@example.com",
                        "password",
                        "John",
                        "Doe",
                        "+1",
                        "1234567890",
                        "Note",
                        AppUserRole.CLIENT,
                        "Company",
                        "TaxNumber",
                        "StreetName",
                        "PostCode",
                        "City",
                        UUID.randomUUID());

        Boolean locked = false;
        Boolean enabled = true;

        User user = new User(userCreateDTO, locked, enabled);

        assertNotNull(user);
        assertEquals(userCreateDTO.getEmail().trim(), user.getEmail());
        assertEquals(userCreateDTO.getFirstName().trim(), user.getFirstName());
        assertEquals(userCreateDTO.getLastName().trim(), user.getLastName());
        assertEquals(userCreateDTO.getPhoneNumberPrefix().trim(), user.getPhoneNumberPrefix());
        assertEquals(userCreateDTO.getPhoneNumber().trim(), user.getPhoneNumber());
        assertEquals(userCreateDTO.getNote().trim(), user.getNote());
        assertEquals(userCreateDTO.getAppUserRole(), user.getAppUserRole());
        assertEquals(locked, user.getLocked());
        assertEquals(enabled, user.getEnabled());
        assertNotNull(user.getCreatedOn());
        assertFalse(user.getIsPasswordChangeRequired());
    }
}