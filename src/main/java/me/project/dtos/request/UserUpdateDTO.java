package me.project.dtos.request;

import me.project.auth.User;
import lombok.Data;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

@Data
public class UserUpdateDTO implements Serializable {
    @NotNull
    private final String Email;

    @NotNull
    private final String FirstName;

    @NotNull
    private final String LastName;

    @NotNull
    private final String PhoneNumberPrefix;

    @NotNull
    private final String PhoneNumber;

    private final String Note;

    private final UUID CompanyId;

    private final String CompanyName;

    private final String StreetName;

    private final String PostCode;

    private final String City;

    public User overrideToUser(User oldUser) {
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
