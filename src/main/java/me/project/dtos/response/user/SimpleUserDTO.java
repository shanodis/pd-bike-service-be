package me.project.dtos.response.user;

import me.project.auth.enums.AppUserRole;
import me.project.dtos.response.address.AddressDTO;
import me.project.dtos.response.company.CompanyDTO;
import me.project.entitiy.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
public class SimpleUserDTO implements Serializable {
    private final UUID userId;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String phoneNumberPrefix;
    private final String phoneNumber;
    private final CompanyDTO company;
    private final AddressDTO address;
    private final AppUserRole appUserRole;

    public static SimpleUserDTO convertFromEntity(User user) {

        CompanyDTO companyDTO = user.getCompany() == null ? null : CompanyDTO.convertFromEntity(user.getCompany());

        AddressDTO addressDTO = user.getAddress() == null ? null : AddressDTO.convertFromEntity(user.getAddress());

        return new SimpleUserDTO(
                user.getUserId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumberPrefix(),
                user.getPhoneNumber(),
                companyDTO,
                addressDTO,
                user.getAppUserRole()
        );
    }
}
