package me.project.service.address;

import me.project.dtos.request.address.AddressUpdateDTO;
import me.project.entitiy.Address;
import me.project.entitiy.Country;
import me.project.entitiy.User;
import me.project.repository.AddressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private AddressService addressService;

    private User user;
    private Country country;

    @BeforeEach
    void setUp() {
        user = User.builder().password("password").email("email").build();
        country = new Country("Poland");
    }

    @Test
    @DisplayName("Should return null when the user has no associated address")
    void getAddressByUserReturnsNullWhenNoAddress() {
        when(addressRepository.getAddressByUser(user)).thenReturn(null);

        Address result = addressService.getAddressByUser(user);

        assertNull(result);
        verify(addressRepository, times(1)).getAddressByUser(user);
    }

    @Test
    @DisplayName("Should return the address associated with the given user")
    void getAddressByUserReturnsAssociatedAddress() {
        Address address = new Address(user, country, "Test Street", "12345", "Test City");
        when(addressRepository.getAddressByUser(user)).thenReturn(address);

        Address result = addressService.getAddressByUser(user);

        assertEquals(address, result);
        verify(addressRepository, times(1)).getAddressByUser(user);
    }

    @Test
    @DisplayName("Should return the address when the address ID exists")
    void getAddressByIdWhenAddressIdExists() {
        UUID addressId = UUID.randomUUID();
        Address address = new Address(user, country, "Test Street", "12345", "Test City");
        address.setAddressId(addressId);

        when(addressRepository.findById(addressId)).thenReturn(Optional.of(address));

        Address result = addressService.getAddressById(addressId);

        assertEquals(address, result);
        verify(addressRepository, times(1)).findById(addressId);
    }

    @Test
    @DisplayName(
            "Should throw a ResponseStatusException with HttpStatus.NOT_FOUND when the address ID does not exist")
    void getAddressByIdWhenAddressIdDoesNotExistThenThrowResponseStatusException() {
        UUID addressId = UUID.randomUUID();

        when(addressRepository.findById(addressId)).thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> {
                            addressService.getAddressById(addressId);
                        });

        assertEquals(exception.getStatus(), HttpStatus.NOT_FOUND);
        assertEquals(
                exception.getReason(),
                "Address with id" + addressId + " doesn't exists in database");

        verify(addressRepository, times(1)).findById(addressId);
    }

    @Test
    @DisplayName("Should return all addresses from the repository")
    void getAddressesReturnsAllAddresses() {
        Address address1 = new Address(user, country, "street1", "12345", "city1");
        Address address2 = new Address(user, country, "street2", "67890", "city2");
        when(addressRepository.findAll()).thenReturn(List.of(address1, address2));

        List<Address> addresses = addressService.getAddresses();

        assertEquals(2, addresses.size());
        assertTrue(addresses.contains(address1));
        assertTrue(addresses.contains(address2));
        verify(addressRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should throw a not found exception when the address ID does not exist")
    void deleteAddressWhenAddressIdDoesNotExistThenThrowNotFoundException() {
        UUID addressId = UUID.randomUUID();

        when(addressRepository.existsById(addressId)).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> addressService.deleteAddress(addressId));

        verify(addressRepository, times(1)).existsById(addressId);
        verify(addressRepository, never()).deleteById(addressId);
    }

    @Test
    @DisplayName("Should delete the address when the address ID exists")
    void deleteAddressWhenAddressIdExists() {
        UUID addressId = UUID.randomUUID();
        Address address = new Address(user, country, "Test Street", "12345", "Test City");
        address.setAddressId(addressId);

        when(addressRepository.existsById(addressId)).thenReturn(true);

        addressService.deleteAddress(addressId);

        verify(addressRepository, times(1)).deleteById(addressId);
    }

    @Test
    @DisplayName("Should throw a ResponseStatusException when the addressId is not found")
    void updateAddressWhenAddressIdNotFoundThenThrowException() {
        UUID addressId = UUID.randomUUID();
        AddressUpdateDTO addressUpdateDTO =
                new AddressUpdateDTO("New Street Name", "New Post Code", "New City", country);

        when(addressRepository.findById(addressId)).thenReturn(Optional.empty());

        assertThrows(
                ResponseStatusException.class,
                () -> {
                    addressService.updateAddress(addressId, addressUpdateDTO);
                });

        verify(addressRepository, times(1)).findById(addressId);
        verifyNoMoreInteractions(addressRepository);
    }

    @Test
    @DisplayName("Should update the address when the addressId is valid")
    void updateAddressWhenAddressIdIsValid() {
        UUID addressId = UUID.randomUUID();
        Address oldAddress = new Address(user, country, "Old Street", "12345", "Old City");
        oldAddress.setAddressId(addressId);
        AddressUpdateDTO addressUpdateDTO =
                new AddressUpdateDTO("New Street", "67890", "New City", country);
        when(addressRepository.findById(addressId)).thenReturn(Optional.of(oldAddress));

        addressService.updateAddress(addressId, addressUpdateDTO);

        verify(addressRepository, times(1)).findById(addressId);
        assertNotNull(oldAddress.getCountry());
        assertEquals(addressUpdateDTO.getStreetName(), oldAddress.getStreetName());
        assertEquals(addressUpdateDTO.getPostCode(), oldAddress.getPostCode());
        assertEquals(addressUpdateDTO.getCity(), oldAddress.getCity());
        assertEquals(addressUpdateDTO.getCountry(), oldAddress.getCountry());
        verify(addressRepository, times(1)).save(oldAddress);
    }

    @Test
    @DisplayName("Should return existing address when the address already exists")
    void createAddressIfNotExistsWhenAddressExists() {
        Address existingAddress = new Address(user, country, "Test Street", "12345", "Test City");
        when(addressRepository.existsByStreetNameAndCityAndPostCodeAndCountry(
                existingAddress.getStreetName(),
                existingAddress.getCity(),
                existingAddress.getPostCode(),
                existingAddress.getCountry()))
                .thenReturn(true);

        when(addressRepository.getByStreetNameAndCityAndPostCodeAndCountry(
                existingAddress.getStreetName(),
                existingAddress.getCity(),
                existingAddress.getPostCode(),
                existingAddress.getCountry()))
                .thenReturn(existingAddress);

        Address result = addressService.createAddressIfNotExists(existingAddress);

        verify(addressRepository, times(0)).save(existingAddress);
        assertEquals(existingAddress, result);
    }

    @Test
    @DisplayName("Should create and return a new address when the address does not exist")
    void createAddressIfNotExistsWhenAddressDoesNotExist() {
        Address newAddress = new Address(user, country, "Test Street", "12345", "Test City");

        when(addressRepository.existsByStreetNameAndCityAndPostCodeAndCountry(
                newAddress.getStreetName(),
                newAddress.getCity(),
                newAddress.getPostCode(),
                newAddress.getCountry()))
                .thenReturn(false);

        when(addressRepository.save(newAddress)).thenReturn(newAddress);

        Address result = addressService.createAddressIfNotExists(newAddress);

        assertNotNull(result);
        assertEquals(newAddress, result);

        verify(addressRepository, times(1))
                .existsByStreetNameAndCityAndPostCodeAndCountry(
                        newAddress.getStreetName(),
                        newAddress.getCity(),
                        newAddress.getPostCode(),
                        newAddress.getCountry());

        verify(addressRepository, times(1)).save(newAddress);
    }
}