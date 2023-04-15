package me.project.dtos.request;

import me.project.auth.enums.AppUserRole;
import lombok.Data;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

@Data
public class UserCreateDTO implements Serializable {
    private final Boolean isEmployee;

    private final String Email;

    private String Password;

    private final String FirstName;

    private final String LastName;

    private final String PhoneNumberPrefix;

    private final String PhoneNumber;

    private final String Tags;

    private final String Note;

    @NotNull
    private final AppUserRole appUserRole;

    private final String CompanyName;

    private final String TaxNumber;

    private final String StreetName;

    private final String PostCode;

    private final String City;

    @Nullable
    private final UUID CountryId;

    public void setPassword(String newPassword) {
        this.Password = newPassword;
    }

}
