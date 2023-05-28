package me.project.dtos.request.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientCreateDTO implements Serializable {
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

    private String CompanyName;

    private String TaxNumber;

    private String StreetName;

    private String PostCode;

    private String City;

    private UUID CountryId;


}
