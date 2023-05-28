package me.project.service.address;

import me.project.auth.User;
import me.project.dtos.request.address.AddressUpdateDTO;
import me.project.entitiy.Address;

import java.util.List;
import java.util.UUID;

public interface IAddressService {

    Address getAddressByUser(User user);

    Address getAddressById(UUID AddressId);

    List<Address> getAddresses();

    Address createAddressIfNotExists(Address NewAddress);

    void updateAddress(UUID AddressId, AddressUpdateDTO addressUpdateDTO);

    void deleteAddress(UUID AddressId);
}
