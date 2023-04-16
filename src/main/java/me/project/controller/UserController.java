package me.project.controller;

import me.project.dtos.request.PageRequestDTO;
import me.project.dtos.request.user.UserCreateDTO;
import me.project.dtos.request.user.UserUpdateDTO;
import me.project.dtos.response.bikes.SimpleBikeDTO;
import me.project.dtos.response.page.PageResponse;
import me.project.dtos.response.user.*;
import me.project.service.bike.IBikeService;
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

    @GetMapping("{userId}/bikes")
    public List<SimpleBikeDTO> getUserBikes(@PathVariable UUID userId, @RequestParam(required = false) String phrase) {
        return bikeService.getBikesByUserAndPhrase(userId, phrase);
    }

    @GetMapping("{userId}/details")
    public CustomerDetailsDTO getUserDetails(@PathVariable UUID userId) {
        return userService.getCustomerDetails(userId);
    }

    @GetMapping("page")
    public PageResponse<SimpleUserDTO> getUsers(@RequestParam Integer pageNumber, @RequestParam Integer pageSize,
                                                @RequestParam String sortDir, @RequestParam String sortBy) {
        return userService.getUsers(new PageRequestDTO(pageNumber, pageSize, sortDir, sortBy));
    }

    @GetMapping("customers/page")
    public PageResponse<SimpleCustomerDTO> getSimpleCustomers(@RequestParam Integer pageNumber, @RequestParam Integer pageSize,
                                                              @RequestParam String sortDir, @RequestParam String sortBy,
                                                              @RequestParam(required = false) String phrase) {
        return userService.getSimpleCustomers(new PageRequestDTO(pageNumber, pageSize, sortDir, sortBy), phrase);
    }

    @GetMapping("employees/page")
    public PageResponse<SimpleEmployeeDTO> getSimpleEmployees(@RequestParam Integer pageNumber, @RequestParam Integer pageSize,
                                                              @RequestParam String sortDir, @RequestParam String sortBy) {
        return userService.getSimpleEmployees(new PageRequestDTO(pageNumber, pageSize, sortDir, sortBy));
    }

    @PostMapping
    public SimpleUserDTO createAppUser(@RequestBody UserCreateDTO userCredentials) {
        return SimpleUserDTO.convertFromEntity(userService.createAppUser(userCredentials));
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
