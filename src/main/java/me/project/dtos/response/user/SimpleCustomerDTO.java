package me.project.dtos.response.user;

import me.project.auth.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleCustomerDTO implements Serializable {
    private UUID userId;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumberPrefix;
    private String phoneNumber;
    private LocalDate createdOn;

    public static SimpleCustomerDTO convertFromEntity(User user) {
        return new SimpleCustomerDTO(
                user.getUserId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumberPrefix(),
                user.getPhoneNumber(),
                user.getCreatedOn()
        );
    }
}
