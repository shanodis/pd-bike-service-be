package me.project.dtos.response.user;

import me.project.entitiy.Address;
import me.project.entitiy.Company;
import me.project.entitiy.Country;
import me.project.entitiy.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
public class CustomerDetailsDTO implements Serializable {
    private final UUID userId;
    private final String firstName;
    private final String lastName;
    private final String phoneNumberPrefix;
    private final String phoneNumber;
    private final String note;
    private final UUID companyId;
    private final String companyName;
    private final String taxNumber;
    private final UUID addressId;
    private final String streetName;
    private final String postCode;
    private final String city;
    private final UUID countryId;
    private final String countryName;

    public static CustomerDetailsDTO convertFromEntity(User user) {

        Company company = user.getCompany();
        Address address = user.getAddress();
        Country country = address.getCountry();

        return new CustomerDetailsDTO(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumberPrefix(),
                user.getPhoneNumber(),
                user.getNote(),
                company.getCompanyId(),
                company.getCompanyName(),
                company.getTaxNumber(),
                address.getAddressId(),
                address.getStreetName(),
                address.getPostCode(),
                address.getCity(),
                country.getCountryId(),
                country.getCountryName()
        );
    }

}
