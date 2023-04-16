package me.project.controller;

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

    @GetMapping("{AddressId}")
    public Address getAddressById(@PathVariable UUID AddressId) {
        return addressService.getAddressById(AddressId);
    }

    @GetMapping
    public List<Address> getAddresses() {
        return addressService.getAddresses();
    }

    @PostMapping
    public Address createAddress(@RequestBody Address NewAddress) {
        return addressService.createAddressIfNotExists(NewAddress);
    }

    @PutMapping("{AddressId}")
    public void updateAddress(@PathVariable UUID AddressId,@RequestBody AddressUpdateDTO addressUpdateDTO) {
        addressService.updateAddress(AddressId, addressUpdateDTO);
    }

    @DeleteMapping("{AddressId}")
    public void deleteAddress(@PathVariable UUID AddressId) {
        addressService.deleteAddress(AddressId);
    }
}
