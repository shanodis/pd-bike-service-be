package me.project.dtos.response.user;

import me.project.entitiy.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("SimpleEmployeeDTO")
class SimpleEmployeeDTOTest {

    @Test
    @DisplayName("Should convert User entity to SimpleEmployeeDTO")
    void convertFromEntityToSimpleEmployeeDTO() {
        UUID userId = UUID.randomUUID();
        String email = "test@example.com";
        String firstName = "John";
        String lastName = "Doe";
        String phoneNumberPrefix = "+1";
        String phoneNumber = "1234567890";
        String note = "Test note";
        User user = User.builder()
                .userId(userId)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .phoneNumberPrefix(phoneNumberPrefix)
                .phoneNumber(phoneNumber)
                .note(note)
                .build();

        SimpleEmployeeDTO simpleEmployeeDTO = SimpleEmployeeDTO.convertFromEntity(user);

        assertNotNull(simpleEmployeeDTO);
        assertEquals(userId, simpleEmployeeDTO.getUserId());
        assertEquals(email, simpleEmployeeDTO.getEmail());
        assertEquals(firstName, simpleEmployeeDTO.getFirstName());
        assertEquals(lastName, simpleEmployeeDTO.getLastName());
        assertEquals(phoneNumberPrefix, simpleEmployeeDTO.getPhoneNumberPrefix());
        assertEquals(phoneNumber, simpleEmployeeDTO.getPhoneNumber());
        assertEquals(note, simpleEmployeeDTO.getNote());
    }

}