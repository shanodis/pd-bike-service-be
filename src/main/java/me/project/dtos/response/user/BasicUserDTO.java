package me.project.dtos.response.user;

import me.project.auth.enums.AppUserRole;
import me.project.entitiy.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
public class BasicUserDTO implements Serializable {
    private final UUID userId;
    private final String firstName;
    private final String lastName;
    @NotNull
    private final AppUserRole appUserRole;

    public static BasicUserDTO convertFromEntity(User user) {
        return new BasicUserDTO(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getAppUserRole()
        );
    }
}
