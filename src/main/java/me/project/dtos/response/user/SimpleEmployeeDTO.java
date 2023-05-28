package me.project.dtos.response.user;

import me.project.entitiy.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
public class SimpleEmployeeDTO implements Serializable {
    private final UUID userId;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String phoneNumberPrefix;
    private final String phoneNumber;
    private final String note;

    public static SimpleEmployeeDTO convertFromEntity(User user) {
        return new SimpleEmployeeDTO(
                user.getUserId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumberPrefix(),
                user.getPhoneNumber(),
                user.getNote()
        );
    }
}
