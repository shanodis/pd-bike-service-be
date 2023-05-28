package me.project.controller;

import me.project.dtos.request.PageRequestDTO;
import me.project.dtos.request.user.ClientCreateDTO;
import me.project.dtos.request.user.UserCreateDTO;
import me.project.dtos.request.user.UserUpdateDTO;
import me.project.dtos.response.bikes.SimpleBikeDTO;
import me.project.dtos.response.page.PageResponse;
import me.project.dtos.response.user.*;
import me.project.service.bike.IBikeService;
import me.project.service.files.IFileService;
import me.project.service.user.IUserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/users")
@AllArgsConstructor
public class UserController {
    private final IUserService userService;
    private final IFileService fileService;
    private final IBikeService bikeService;

    @GetMapping
    public List<SimpleUserDTO> getUsers() {
        return userService.getUsers()
                .stream()
                .map(SimpleUserDTO::convertFromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("{userId}")
    public BasicUserDTO getUser(@PathVariable UUID userId) {
        return BasicUserDTO.convertFromEntity(userService.getUser(userId));
    }

    @GetMapping("{userId}/avatar")
    public String getUserAvatar(@PathVariable UUID userId) {
        return fileService.getFileUrl(userService.getUser(userId).getAvatar().getFileId());
    }

    @GetMapping("{userId}/bikes")
    public List<SimpleBikeDTO> getUserBikes(@PathVariable UUID userId, @RequestParam(required = false,defaultValue = "") String phrase) {
        return bikeService.getBikesByUserAndPhrase(userId, phrase);
    }

    @GetMapping("{userId}/details")
    public CustomerDetailsDTO getUserDetails(@PathVariable UUID userId) {
        return userService.getCustomerDetails(userId);
    }

    @GetMapping("page")
    public PageResponse<SimpleUserDTO> getUsers(@RequestParam Integer page, @RequestParam Integer pageLimit,
                                                @RequestParam String sortDir, @RequestParam String sortBy) {
        return userService.getUsers(new PageRequestDTO(page, pageLimit, sortDir, sortBy));
    }

    @GetMapping("customers/page")
    public PageResponse<SimpleCustomerDTO> getSimpleCustomers(@RequestParam Integer page, @RequestParam Integer pageLimit,
                                                              @RequestParam String sortDir, @RequestParam String sortBy,
                                                              @RequestParam(required = false) String phrase) {
        return userService.getSimpleCustomers(new PageRequestDTO(page, pageLimit, sortDir, sortBy), phrase);
    }

    @GetMapping("employees/page")
    public PageResponse<SimpleEmployeeDTO> getSimpleEmployees(@RequestParam Integer page, @RequestParam Integer pageLimit,
                                                              @RequestParam String sortDir, @RequestParam String sortBy) {
        return userService.getSimpleEmployees(new PageRequestDTO(page, pageLimit, sortDir, sortBy));
    }

    @PostMapping
    public SimpleUserDTO createAppUser(@RequestBody UserCreateDTO userCredentials) {
        return SimpleUserDTO.convertFromEntity(userService.createAppUser(userCredentials));
    }

    @PostMapping("add-customer")
    public UUID createCustomer(@RequestBody ClientCreateDTO clientCreateDTO) {
        return userService.createCustomer(clientCreateDTO);
    }

    @PutMapping("{userId}")
    public void updateAppUser(@PathVariable UUID userId, @RequestBody UserUpdateDTO userCredentials) {
        userService.updateAppUser(userId, userCredentials);
    }

    @DeleteMapping("{userId}")
    public void deleteUserById(@PathVariable UUID userId) {
        userService.deleteUserById(userId);
    }
}
