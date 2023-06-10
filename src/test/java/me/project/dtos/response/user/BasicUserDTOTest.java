package me.project.dtos.response.user;

import me.project.auth.enums.AppUserRole;
import me.project.entitiy.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("BasicUserDTO")
class BasicUserDTOTest {

    @Test
    @DisplayName("Should convert User entity to BasicUserDTO")
    void convertFromEntityToBasicUserDTO() {
        UUID userId = UUID.randomUUID();
        String firstName = "John";
        String lastName = "Doe";
        String note = "Some note";
        AppUserRole appUserRole = AppUserRole.CLIENT;

        User user = User.builder()
                .userId(userId)
                .firstName(firstName)
                .lastName(lastName)
                .note(note)
                .appUserRole(appUserRole)
                .build();

        BasicUserDTO basicUserDTO = BasicUserDTO.convertFromEntity(user);

        assertNotNull(basicUserDTO);
        assertEquals(userId, basicUserDTO.getUserId());
        assertEquals(firstName, basicUserDTO.getFirstName());
        assertEquals(lastName, basicUserDTO.getLastName());
        assertEquals(note, basicUserDTO.getNote());
        assertEquals(appUserRole, basicUserDTO.getAppUserRole());
    }

}