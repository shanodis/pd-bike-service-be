package me.project.repository;

import me.project.auth.User;
import me.project.entitiy.Address;
import me.project.entitiy.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID>, JpaSpecificationExecutor<Address> {

    boolean existsByStreetNameAndCityAndPostCodeAndCountry(String streetName, String city, String postCode, Country country);

    Address getByStreetNameAndCityAndPostCodeAndCountry(String streetName, String city, String postCode, Country country);

    Address getAddressByUser(User user);
}