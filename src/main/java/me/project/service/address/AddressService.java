package me.project.service.address;

import me.project.entitiy.User;
import me.project.dtos.request.address.AddressUpdateDTO;
import me.project.entitiy.Address;
import me.project.repository.AddressRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AddressService implements IAddressService {

    private final AddressRepository addressRepository;

    private String NOT_FOUND(UUID AddressId) {
        return "Address with id" + AddressId + " doesn't exists in database";
    }

    @Override
    public Address getAddressByUser(User user) {
        return addressRepository.getAddressByUser(user);
    }

    public Address getAddressById(UUID AddressId) {
        return addressRepository.findById(AddressId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NOT_FOUND(AddressId))
        );
    }

    public List<Address> getAddresses() {
        return addressRepository.findAll();
    }

    public Address createAddressIfNotExists(Address newAddress) {

        if(addressRepository.existsByStreetNameAndCityAndPostCodeAndCountry(
                newAddress.getStreetName(),
                newAddress.getCity(),
                newAddress.getPostCode(),
                newAddress.getCountry()
        )){
            return addressRepository.getByStreetNameAndCityAndPostCodeAndCountry(
                    newAddress.getStreetName(),
                    newAddress.getCity(),
                    newAddress.getPostCode(),
                    newAddress.getCountry()
            );
        }

        addressRepository.save(newAddress);

        return newAddress;
    }

    public void updateAddress(UUID AddressId, AddressUpdateDTO addressUpdateDTO) {
        Address oldAddress = addressRepository.findById(AddressId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NOT_FOUND(AddressId))
        );

        oldAddress = addressUpdateDTO.convertToEntity(oldAddress);

        addressRepository.save(oldAddress);
    }

    public void deleteAddress(UUID AddressId) {
        if(!addressRepository.existsById(AddressId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, NOT_FOUND(AddressId));

        addressRepository.deleteById(AddressId);
    }
}
