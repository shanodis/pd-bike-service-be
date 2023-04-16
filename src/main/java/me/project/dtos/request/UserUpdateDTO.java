package me.project.dtos.request;

import me.project.auth.enums.AppUserRole;
import me.project.entitiy.Address;
import me.project.entitiy.Company;
import lombok.Data;
import me.project.entitiy.User;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

@Data
public class UserUpdateDTO implements Serializable {
    private final UUID UserId;

    private final String Email;

    private final String FirstName;

    private final String LastName;

    private final String PhoneNumberPrefix;

    private final String PhoneNumber;

    private final String Tags;

    private final String Note;

    @NotNull
    private final AppUserRole appUserRole;

    private final Company Company;

    private final Address Address;

    public User overrideToUser(User oldUser){
        if (!getEmail().isEmpty())
            oldUser.setEmail(getEmail());

        oldUser.setFirstName(FirstName);
        oldUser.setLastName(LastName);
        oldUser.setPhoneNumberPrefix(PhoneNumberPrefix);
        oldUser.setPhoneNumber(PhoneNumber);
        oldUser.setNote(Note);

        return oldUser;
    }
}
