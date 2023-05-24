package me.project.dtos.request;

import me.project.auth.enums.AppUserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDTO implements Serializable {

    private String Email;

    private String Password;

    private String FirstName;

    private String LastName;

    private String PhoneNumberPrefix;

    private String PhoneNumber;

    private String Tags;

    private String Note;

    @NotNull
    private AppUserRole appUserRole;

    private String CompanyName;

    private String TaxNumber;

    private String StreetName;

    private String PostCode;

    private String City;

    @Nullable
    private UUID CountryId;

    public void setPassword(String newPassword) {
        this.Password = newPassword;
    }

}
