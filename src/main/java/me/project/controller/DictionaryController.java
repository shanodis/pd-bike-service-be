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
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(path = "api/v1/dictionaries")
@AllArgsConstructor
public class DictionaryController {

    private final IUserService userService;
    private final IBikeService bikeService;
    private final ICountryService countryService;
    private final IOrderStatusService orderStatus;
    private final IServiceService serviceService;

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_CLIENT"})
    @GetMapping("bikes")
    public PageResponse<DictionaryResponseDTO> getBikes(@RequestParam Integer page, @RequestParam Integer pageLimit,
                                                        @RequestParam String sortDir, @RequestParam String sortBy,
                                                        @RequestParam(required = false, defaultValue = "") UUID userId) {
        return bikeService.getBikesDictionary(new PageRequestDTO(page, pageLimit, sortDir, sortBy), userId);
    }

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_CLIENT"})
    @GetMapping("countries")
    public PageResponse<CountryWithoutAddressesDTO> getCountries(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageLimit,
            @RequestParam(required = false) String sortDir,
            @RequestParam(required = false, defaultValue = "countryName") String sortBy) {
        return countryService.getAllCountries(new PageRequestDTO(page, pageLimit, sortDir, sortBy));
    }

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_CLIENT"})
    @GetMapping("order-statuses")
    public PageResponse<DictionaryResponseDTO> getStatuses(@RequestParam Integer page, @RequestParam Integer pageLimit,
                                                           @RequestParam String sortDir, @RequestParam String sortBy) {
        return orderStatus.getAllStatusesDictionary(new PageRequestDTO(page, pageLimit, sortDir, sortBy));
    }

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_CLIENT"})
    @GetMapping("services")
    public PageResponse<ServiceDTO> getServices(@RequestParam Integer page, @RequestParam Integer pageLimit,
                                                @RequestParam String sortDir, @RequestParam String sortBy,
                                                @RequestParam(required = false, defaultValue = "") String phrase) {
        return serviceService.getAllServicesDictionary(new PageRequestDTO(page, pageLimit, sortDir, sortBy), phrase);
    }

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE"})
    @GetMapping("users")
    public PageResponse<DictionaryResponseDTO> getUsers(@RequestParam Integer page, @RequestParam Integer pageLimit,
                                                        @RequestParam String sortDir, @RequestParam String sortBy) {
        return userService.getUsersDictionary(new PageRequestDTO(page, pageLimit, sortDir, sortBy));
    }
}