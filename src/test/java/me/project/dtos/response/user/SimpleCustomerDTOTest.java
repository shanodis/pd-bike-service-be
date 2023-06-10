package me.project.dtos.response.user;

import me.project.entitiy.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("SimpleCustomerDTO")
class SimpleCustomerDTOTest {

    @Test
    @DisplayName("Should convert User entity to SimpleCustomerDTO with correct fields and lastServiceOn")
    void convertFromEntityWithCorrectFieldsAndLastServiceOn() {
        UUID userId = UUID.randomUUID();
        String email = "test@example.com";
        String firstName = "John";
        String lastName = "Doe";
        String phoneNumberPrefix = "+1";
        String phoneNumber = "1234567890";
        LocalDateTime createdOn = LocalDateTime.now();
        LocalDateTime lastServiceOn = LocalDateTime.now().minusDays(1);

        User user = User.builder()
                .userId(userId)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .phoneNumberPrefix(phoneNumberPrefix)
                .phoneNumber(phoneNumber)
                .createdOn(createdOn)
                .build();

        SimpleCustomerDTO simpleCustomerDTO = SimpleCustomerDTO.convertFromEntity(user, lastServiceOn);

        assertEquals(userId, simpleCustomerDTO.getUserId());
        assertEquals(email, simpleCustomerDTO.getEmail());
        assertEquals(firstName, simpleCustomerDTO.getFirstName());
        assertEquals(lastName, simpleCustomerDTO.getLastName());
        assertEquals(phoneNumberPrefix, simpleCustomerDTO.getPhoneNumberPrefix());
        assertEquals(phoneNumber, simpleCustomerDTO.getPhoneNumber());
        assertEquals(createdOn, simpleCustomerDTO.getCreatedOn());
        assertEquals(lastServiceOn, simpleCustomerDTO.getLastServiceOn());
    }

}