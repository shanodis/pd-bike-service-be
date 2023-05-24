package me.project.dtos.request.user;

import me.project.auth.User;
import me.project.auth.enums.AppUserRole;
import me.project.entitiy.Address;
import me.project.entitiy.Company;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO implements Serializable {

    @NotNull
    private String Email;

    @NotNull
    private String FirstName;

    @NotNull
    private String LastName;

    @NotNull
    private String PhoneNumberPrefix;

    @NotNull
    private String PhoneNumber;

    private String Note;

    private UUID CompanyId;

    private String CompanyName;

    private String TaxNumber;

    private String StreetName;

    private String PostCode;

    private String City;

    public User convertToUser(User oldUser) {
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
