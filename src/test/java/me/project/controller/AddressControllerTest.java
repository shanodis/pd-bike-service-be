package me.project.controller;

import me.project.auth.enums.AppUserRole;
import me.project.dtos.request.address.AddressUpdateDTO;
import me.project.entitiy.Address;
import me.project.entitiy.Country;
import me.project.entitiy.User;
import me.project.service.address.IAddressService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Address Controller Tests")
class AddressControllerTest {
    @Mock
    private IAddressService addressService;

    @InjectMocks
    private AddressController addressController;

    @Test
    @DisplayName("Should return all addresses")
    void getAddressesReturnsAllAddresses() {
        Address address1 = new Address(
                new User("test1@example.com", "John", "Doe", AppUserRole.CLIENT, false, true),
                null, "Test Street 1", "12345", "Test City 1");
        Address address2 = new Address(
                new User("test2@example.com", "Jane", "Doe", AppUserRole.CLIENT, false, true),
                null, "Test Street 2", "67890", "Test City 2");
        List<Address> addresses = List.of(address1, address2);

        when(addressService.getAddresses()).thenReturn(addresses);

        List<Address> result = addressController.getAddresses();

        assertEquals(addresses, result);
        verify(addressService, times(1)).getAddresses();
    }

    @Test
    @DisplayName("Should return the address when the address ID is valid")
    void getAddressByIdWhenAddressIdIsValid() {
        UUID addressId = UUID.randomUUID();
        Address address = new Address();
        address.setAddressId(addressId);
        when(addressService.getAddressById(addressId)).thenReturn(address);

        Address result = addressController.getAddressById(addressId);

        assertEquals(address, result);
        verify(addressService, times(1)).getAddressById(addressId);
    }
}