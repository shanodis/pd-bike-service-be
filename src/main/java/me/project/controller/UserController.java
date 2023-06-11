package me.project.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import me.project.dtos.request.PageRequestDTO;
import me.project.dtos.request.user.ClientCreateDTO;
import me.project.dtos.request.user.Toggle2FADTO;
import me.project.dtos.request.user.UserCreateDTO;
import me.project.dtos.request.user.UserUpdateDTO;
import me.project.dtos.response.bikes.SimpleBikeDTO;
import me.project.dtos.response.page.PageResponse;
import me.project.dtos.response.user.*;
import me.project.service.auth.TotpService;
import me.project.service.bike.IBikeService;
import me.project.service.files.IFileService;
import me.project.service.user.IUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/users")
@AllArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {
    private final IUserService userService;
    private final IFileService fileService;
    private final IBikeService bikeService;
    private final TotpService totpService;

    @PatchMapping("toggle2FA")
    public ResponseEntity<?> toggle2FA(Principal principal, @RequestBody Toggle2FADTO toggle2FADTO) {
        return totpService.toggle2FA(principal, toggle2FADTO);
    }

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
        return userService.getUserAvatar(userId);
    }

    @GetMapping("{userId}/bikes")
    public List<SimpleBikeDTO> getUserBikes(@PathVariable UUID userId, @RequestParam(required = false, defaultValue = "") String phrase) {
        return bikeService.getBikesByUserAndPhrase(userId, phrase);
    }

    @GetMapping("{userId}/details")
    public CustomerDetailsDTO getUserDetails(@PathVariable UUID userId) {
        return userService.getCustomerDetails(userId);
    }

    @GetMapping("page")
    public PageResponse<SimpleUserDTO> getUsers(@RequestParam Integer page, @RequestParam Integer pageLimit,
                                                @RequestParam(required = false, defaultValue = "asc") String sortDir,
                                                @RequestParam(required = false, defaultValue = "firstName") String sortBy) {
        return userService.getUsers(new PageRequestDTO(page, pageLimit, sortDir, sortBy));
    }

    @GetMapping("customers/page")
    public PageResponse<SimpleCustomerDTO> getSimpleCustomers(@RequestParam Integer page, @RequestParam Integer pageLimit,
                                                              @RequestParam(required = false, defaultValue = "asc") String sortDir,
                                                              @RequestParam(required = false, defaultValue = "firstName") String sortBy,
                                                              @RequestParam(required = false) String phrase) {
        return userService.getSimpleCustomers(new PageRequestDTO(page, pageLimit, sortDir, sortBy), phrase);
    }

    @GetMapping("employees/page")
    public PageResponse<SimpleEmployeeDTO> getSimpleEmployees(@RequestParam Integer page, @RequestParam Integer pageLimit,
                                                              @RequestParam(required = false, defaultValue = "asc") String sortDir,
                                                              @RequestParam(required = false, defaultValue = "firstName") String sortBy) {
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

    @PostMapping(value = "{userId}/avatar", consumes = {"multipart/form-data"})
    public void uploadUserAvatar(@PathVariable UUID userId, @RequestParam("file") MultipartFile file) {
        userService.uploadUserAvatar(userId, file);
    }

    @PutMapping("{userId}")
    public void updateAppUser(@PathVariable UUID userId, @RequestBody UserUpdateDTO userCredentials) {
        userService.updateAppUser(userId, userCredentials);
    }

    @PutMapping(value = "{userId}/avatar", consumes = {"multipart/form-data"})
    public void updateUserAvatar(@PathVariable UUID userId, @RequestParam("file") MultipartFile file) {
        userService.updateUserAvatar(userId, file);
    }

    @DeleteMapping("{userId}")
    public void deleteUserById(@PathVariable UUID userId) {
        userService.deleteUserById(userId);
    }

    @DeleteMapping("{userId}/avatar")
    public void deleteUserAvatar(@PathVariable UUID userId) {
        userService.deleteUserAvatar(userId);
    }
}
