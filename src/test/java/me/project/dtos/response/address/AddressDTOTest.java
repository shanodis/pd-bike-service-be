package me.project.dtos.response.address;

import me.project.entitiy.Address;
import me.project.entitiy.Country;
import me.project.entitiy.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("AddressDTO should")
class AddressDTOTest {

    @Test
    @DisplayName("Should convert Address entity to AddressDTO")
    void convertFromEntityToAddressDTO() {
        User user = new User();
        Country country = new Country("USA");
        Address address = new Address(user, country, "Main Street", "12345", "New York");
        address.setAddressId(UUID.randomUUID());

        AddressDTO addressDTO = AddressDTO.convertFromEntity(address);

        assertNotNull(addressDTO);
        assertEquals(address.getAddressId(), addressDTO.getAddressId());
        assertEquals(address.getCountry().getCountryName(), addressDTO.getCountry().getCountryName());
        assertEquals(address.getStreetName(), addressDTO.getStreetName());
        assertEquals(address.getPostCode(), addressDTO.getPostCode());
        assertEquals(address.getCity(), addressDTO.getCity());
    }

}