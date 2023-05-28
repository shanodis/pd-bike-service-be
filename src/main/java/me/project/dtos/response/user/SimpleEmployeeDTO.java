package me.project.dtos.response.user;

import me.project.auth.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleEmployeeDTO implements Serializable {
    private UUID userId;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumberPrefix;
    private String phoneNumber;

    public static SimpleEmployeeDTO convertFromEntity(User user) {
        return new SimpleEmployeeDTO(
                user.getUserId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumberPrefix(),
                user.getPhoneNumber()
        );
    }
}
