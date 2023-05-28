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
    private UUID userId;
    private String firstName;
    private String lastName;
    private String phoneNumberPrefix;
    private String phoneNumber;
    private String email;
    private String note;
    private UUID companyId;
    private String companyName;
    private String taxNumber;
    private UUID addressId;
    private String streetName;
    private String postCode;
    private String city;
    private UUID countryId;
    private String countryName;

    public CustomerDetailsDTO(UUID userId,
                              String firstName,
                              String lastName,
                              String phoneNumberPrefix,
                              String phoneNumber,
                              String note,
                              String email) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumberPrefix = phoneNumberPrefix;
        this.phoneNumber = phoneNumber;
        this.note = note;
        this.email = email;
    }

    public CustomerDetailsDTO(UUID userId,
                              String firstName,
                              String lastName,
                              String phoneNumberPrefix,
                              String phoneNumber,
                              String note,
                              UUID companyId,
                              String companyName,
                              String taxNumber,
                              String email) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumberPrefix = phoneNumberPrefix;
        this.phoneNumber = phoneNumber;
        this.note = note;
        this.companyId = companyId;
        this.companyName = companyName;
        this.taxNumber = taxNumber;
        this.email = email;
    }

    public CustomerDetailsDTO(UUID userId,
                              String firstName,
                              String lastName,
                              String phoneNumberPrefix,
                              String phoneNumber,
                              String note,
                              UUID addressId,
                              String streetName,
                              String postCode,
                              String city,
                              UUID countryId,
                              String countryName,
                              String email) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumberPrefix = phoneNumberPrefix;
        this.phoneNumber = phoneNumber;
        this.note = note;
        this.addressId = addressId;
        this.streetName = streetName;
        this.postCode = postCode;
        this.city = city;
        this.countryId = countryId;
        this.countryName = countryName;
        this.email = email;
    }

    public static CustomerDetailsDTO convertFromEntity(User user) {

        Company company = user.getCompany();
        boolean isCompanyPresent = company != null;

        Address address = user.getAddress();
        boolean isAddressPresent = address != null;

        Country country = null;
        if (isAddressPresent) {
            country = address.getCountry();
        }

        if (!isCompanyPresent && !isAddressPresent)
            return new CustomerDetailsDTO(
                    user.getUserId(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getPhoneNumberPrefix(),
                    user.getPhoneNumber(),
                    user.getNote(),
                    user.getEmail()
            );
        else if (isCompanyPresent && !isAddressPresent)
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
                    user.getEmail()
            );
        else if (!isCompanyPresent)
            return new CustomerDetailsDTO(
                    user.getUserId(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getPhoneNumberPrefix(),
                    user.getPhoneNumber(),
                    user.getNote(),
                    address.getAddressId(),
                    address.getStreetName(),
                    address.getPostCode(),
                    address.getCity(),
                    country.getCountryId(),
                    country.getCountryName(),
                    user.getEmail()
            );
        else return new CustomerDetailsDTO(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumberPrefix(),
                user.getPhoneNumber(),
                user.getEmail(),
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
