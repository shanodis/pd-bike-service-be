package me.project.service.address;

import me.project.repository.AddressRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AddressService implements IAddressService {
    private final AddressRepository addressRepository;
}
