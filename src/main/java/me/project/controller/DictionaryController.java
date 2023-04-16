package me.project.controller;

import me.project.dtos.request.PageRequestDTO;
import me.project.dtos.response.DictionaryResponseDTO;
import me.project.dtos.response.country.CountryWithoutAddressesDTO;
import me.project.dtos.response.page.PageResponse;
import me.project.dtos.response.services.ServiceDTO;
import me.project.service.bike.IBikeService;
import me.project.service.country.ICountryService;
import me.project.service.order.status.IOrderStatusService;
import me.project.service.service.IServiceService;
import me.project.service.user.IUserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/v1/dictionaries")
@AllArgsConstructor
public class DictionaryController {

    private final IUserService userService;
    private final IBikeService bikeService;
    private final ICountryService countryService;
    private final IOrderStatusService orderStatus;
    private final IServiceService serviceService;

    @GetMapping("bikes")
    public PageResponse<DictionaryResponseDTO> getBikes(@RequestParam Integer pageNumber, @RequestParam Integer pageSize,
                                                        @RequestParam String sortDir, @RequestParam String sortBy) {
        return bikeService.getBikesDictionary(new PageRequestDTO(pageNumber, pageSize, sortDir, sortBy));
    }

    @GetMapping("countries")
    public PageResponse<CountryWithoutAddressesDTO> getCountries(@RequestParam Integer pageNumber, @RequestParam Integer pageSize,
                                                         @RequestParam String sortDir, @RequestParam String sortBy) {
        return countryService.getAllCountries(new PageRequestDTO(pageNumber, pageSize, sortDir, sortBy));
    }

    @GetMapping("order-statuses")
    public PageResponse<DictionaryResponseDTO> getStatuses(@RequestParam Integer pageNumber, @RequestParam Integer pageSize,
                                                           @RequestParam String sortDir, @RequestParam String sortBy) {
        return orderStatus.getAllStatusesDictionary(new PageRequestDTO(pageNumber, pageSize, sortDir, sortBy));
    }

    @GetMapping("services")
    public PageResponse<ServiceDTO> getServices(@RequestParam Integer pageNumber, @RequestParam Integer pageSize,
                                                @RequestParam String sortDir, @RequestParam String sortBy) {
        return serviceService.getAllServicesDictionary(new PageRequestDTO(pageNumber, pageSize, sortDir, sortBy));
    }

    @GetMapping("users")
    public PageResponse<DictionaryResponseDTO> getUsers(@RequestParam Integer pageNumber, @RequestParam Integer pageSize,
                                                        @RequestParam String sortDir, @RequestParam String sortBy) {
        return userService.getUsersDictionary(new PageRequestDTO(pageNumber, pageSize, sortDir, sortBy));
    }

}
