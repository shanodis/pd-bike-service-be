package me.project.service.address;

import me.project.dtos.request.address.AddressCreateDTO;
import me.project.entitiy.User;
import me.project.dtos.request.address.AddressUpdateDTO;
import me.project.entitiy.Address;

import java.util.List;
import java.util.UUID;

public interface IAddressService {

    Address getAddressByUser(User user);

    Address getAddressById(UUID addressId);

    List<Address> getAddresses();

    Address createAddressIfNotExists(AddressCreateDTO newAddress);

    void updateAddress(UUID addressId, AddressUpdateDTO addressUpdateDTO);

    void deleteAddress(UUID addressId);
}
