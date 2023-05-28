package me.project.dtos.response.user;

import me.project.entitiy.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class SimpleCustomerDTO implements Serializable {
    private final UUID userId;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String phoneNumberPrefix;
    private final String phoneNumber;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime createdOn;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime lastServiceOn;

    public static SimpleCustomerDTO convertFromEntity(User user, LocalDateTime lastServiceOn) {
        return new SimpleCustomerDTO(
                user.getUserId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumberPrefix(),
                user.getPhoneNumber(),
                user.getCreatedOn(),
                lastServiceOn
        );
    }
}
