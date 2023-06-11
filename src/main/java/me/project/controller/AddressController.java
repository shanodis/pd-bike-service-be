package me.project.controller;

import me.project.dtos.request.address.AddressCreateDTO;
import me.project.dtos.request.address.AddressUpdateDTO;
import me.project.entitiy.Address;
import me.project.service.address.IAddressService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/addresses")
@AllArgsConstructor
public class AddressController {
    private final IAddressService addressService;

    @GetMapping("{addressId}")
    public Address getAddressById(@PathVariable UUID addressId) {
        return addressService.getAddressById(addressId);
    }

    @GetMapping
    public List<Address> getAddresses() {
        return addressService.getAddresses();
    }

    @PostMapping
    public Address createAddress(@RequestBody AddressCreateDTO newAddress) {
        return addressService.createAddressIfNotExists(newAddress);
    }

    @PutMapping("{addressId}")
    public void updateAddress(@PathVariable UUID addressId,@RequestBody AddressUpdateDTO addressUpdateDTO) {
        addressService.updateAddress(addressId, addressUpdateDTO);
    }

    @DeleteMapping("{addressId}")
    public void deleteAddress(@PathVariable UUID addressId) {
        addressService.deleteAddress(addressId);
    }
}
