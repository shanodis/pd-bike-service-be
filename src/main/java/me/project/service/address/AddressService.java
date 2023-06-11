package me.project.service.address;

import me.project.dtos.request.address.AddressCreateDTO;
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

    private String notFound(UUID addressId) {
        return "Address with id" + addressId + " doesn't exists in database";
    }

    @Override
    public Address getAddressByUser(User user) {
        return addressRepository.getAddressByUser(user);
    }

    public Address getAddressById(UUID addressId) {
        return addressRepository.findById(addressId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, notFound(addressId))
        );
    }

    public List<Address> getAddresses() {
        return addressRepository.findAll();
    }

    public Address createAddressIfNotExists(AddressCreateDTO newAddress) {

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

        return addressRepository.save(new Address(
                newAddress.getUser(),
                newAddress.getCountry(),
                newAddress.getStreetName(),
                newAddress.getPostCode(),
                newAddress.getCity()
        ));
    }

    public void updateAddress(UUID addressId, AddressUpdateDTO addressUpdateDTO) {
        Address oldAddress = addressRepository.findById(addressId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, notFound(addressId))
        );

        oldAddress = addressUpdateDTO.convertToEntity(oldAddress);

        addressRepository.save(oldAddress);
    }

    public void deleteAddress(UUID addressId) {
        if(!addressRepository.existsById(addressId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, notFound(addressId));

        addressRepository.deleteById(addressId);
    }
}
