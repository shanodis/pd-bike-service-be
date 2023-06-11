package me.project.controller;

import me.project.dtos.request.address.AddressCreateDTO;
import me.project.dtos.request.address.AddressUpdateDTO;
import me.project.entitiy.Address;
import me.project.service.address.IAddressService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/addresses")
@AllArgsConstructor
public class AddressController {
    private final IAddressService addressService;

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_CLIENT"})
    @GetMapping("{addressId}")
    public Address getAddressById(@PathVariable UUID addressId) {
        return addressService.getAddressById(addressId);
    }

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE"})
    @GetMapping
    public List<Address> getAddresses() {
        return addressService.getAddresses();
    }

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE"})
    @PostMapping
    public Address createAddress(@RequestBody AddressCreateDTO newAddress) {
        return addressService.createAddressIfNotExists(newAddress);
    }

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE"})
    @PutMapping("{addressId}")
    public void updateAddress(@PathVariable UUID addressId,@RequestBody AddressUpdateDTO addressUpdateDTO) {
        addressService.updateAddress(addressId, addressUpdateDTO);
    }

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE"})
    @DeleteMapping("{addressId}")
    public void deleteAddress(@PathVariable UUID addressId) {
        addressService.deleteAddress(addressId);
    }
}