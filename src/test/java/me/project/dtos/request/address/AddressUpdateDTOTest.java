package me.project.dtos.request.address;

import me.project.entitiy.Address;
import me.project.entitiy.Country;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("AddressUpdateDTO")
class AddressUpdateDTOTest {

    @Test
    public void testConvertToEntity() {
        // Create an instance of the Address class
        UUID addressId = UUID.randomUUID();
        Address address = new Address();
        address.setAddressId(addressId);
        address.setStreetName("Old Street");
        address.setPostCode("12345");
        address.setCity("Old City");
        address.setCountry(new Country());

        // Create an instance of the AddressUpdateDTO class
        AddressUpdateDTO addressUpdateDTO = new AddressUpdateDTO();
        addressUpdateDTO.setStreetName("New Street");
        addressUpdateDTO.setPostCode("54321");
        addressUpdateDTO.setCity("New City");
        addressUpdateDTO.setCountry(new Country("Poland"));

        // Call the convertToEntity method
        Address updatedAddress = addressUpdateDTO.convertToEntity(address);

        // Verify that the address object was updated correctly
        assertEquals("New Street", updatedAddress.getStreetName());
        assertEquals("54321", updatedAddress.getPostCode());
        assertEquals("New City", updatedAddress.getCity());
        assertEquals(new Country("Poland").getCountryName(), updatedAddress.getCountry().getCountryName());
        assertEquals(addressId, updatedAddress.getAddressId());
    }

    @Test
    public void testConvertToEntityWithNullStreetName() {
        // Create an instance of the Address class
        Address address = new Address();
        address.setAddressId(UUID.randomUUID());
        address.setStreetName("Old Street");
        address.setPostCode("12345");
        address.setCity("Old City");
        address.setCountry(new Country());

        // Create an instance of the AddressUpdateDTO class with a null streetName
        AddressUpdateDTO addressUpdateDTO = new AddressUpdateDTO();
        addressUpdateDTO.setStreetName(null);
        addressUpdateDTO.setPostCode("54321");
        addressUpdateDTO.setCity("New City");
        addressUpdateDTO.setCountry(new Country());

        // Call the convertToEntity method
        addressUpdateDTO.convertToEntity(address);
    }

    @Test
    public void testConvertToEntityWithNullPostCode() {
        // Create an instance of the Address class
        Address address = new Address();
        address.setAddressId(UUID.randomUUID());
        address.setStreetName("Old Street");
        address.setPostCode("12345");
        address.setCity("Old City");
        address.setCountry(new Country());

        // Create an instance of the AddressUpdateDTO class with a null postCode
        AddressUpdateDTO addressUpdateDTO = new AddressUpdateDTO();
        addressUpdateDTO.setStreetName("New Street");
        addressUpdateDTO.setPostCode(null);
        addressUpdateDTO.setCity("New City");
        addressUpdateDTO.setCountry(new Country());

        // Call the convertToEntity method
        addressUpdateDTO.convertToEntity(address);
    }

    @Test
    public void testConvertToEntityWithNullCity() {
        // Create an instance of the Address class
        Address address = new Address();
        address.setAddressId(UUID.randomUUID());
        address.setStreetName("Old Street");
        address.setPostCode("12345");
        address.setCity("Old City");
        address.setCountry(new Country());

        // Create an instance of the AddressUpdateDTO class with a null city
        AddressUpdateDTO addressUpdateDTO = new AddressUpdateDTO();
        addressUpdateDTO.setStreetName("New Street");
        addressUpdateDTO.setPostCode("54321");
        addressUpdateDTO.setCity(null);
        addressUpdateDTO.setCountry(new Country());

        // Call the convertToEntity method
        addressUpdateDTO.convertToEntity(address);
    }

    @Test
    public void testConvertToEntityWithNullCountry() {
        // Create an instance of the Address class
        Address address = new Address();
        address.setAddressId(UUID.randomUUID());
        address.setStreetName("Old Street");
        address.setPostCode("12345");
        address.setCity("Old City");
        address.setCountry(new Country());

        // Create an instance of the AddressUpdateDTO class with a null country
        AddressUpdateDTO addressUpdateDTO = new AddressUpdateDTO();
        addressUpdateDTO.setStreetName("New Street");
        addressUpdateDTO.setPostCode("54321");
        addressUpdateDTO.setCity("New City");
        addressUpdateDTO.setCountry(null);

        // Call the convertToEntity method
        addressUpdateDTO.convertToEntity(address);
    }

}