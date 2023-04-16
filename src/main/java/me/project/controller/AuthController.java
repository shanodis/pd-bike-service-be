package me.project.controller;

import me.project.dtos.request.user.ChangePasswordDTO;
import me.project.dtos.request.user.CustomerRegisterDTO;
import me.project.dtos.request.user.NewPasswordDTO;
import me.project.entitiy.User;
import me.project.service.user.IUserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/auth")
@AllArgsConstructor
public class AuthController {
    private final IUserService iUserService;

    @PostMapping("register")
    public User registerCustomer(@RequestBody CustomerRegisterDTO customerRegisterDTO) {
        return iUserService.registerCustomer(customerRegisterDTO);
    }

    @PostMapping("employee-create")
    public User createEmployee(@RequestBody CustomerRegisterDTO employeeRegisterDTO) {
        return iUserService.createEmployee(employeeRegisterDTO);
    }

    @PutMapping("{id}/password")
    public void changeUserPassword(@PathVariable("id") UUID userId, @RequestBody ChangePasswordDTO changePasswordDTO) {
        iUserService.changeUserPassword(userId, changePasswordDTO);
    }

    @PatchMapping("{id}/password")
    public void setPasswordAndActivateAccount(@PathVariable("id") UUID userId, @RequestBody NewPasswordDTO newPasswordDTO) {
        iUserService.activateAndSetPassword(userId, newPasswordDTO);
    }

    @PatchMapping("{id}/change-user-state")
    public void changeStateOfUser(@PathVariable("id") UUID userId) {
        iUserService.changeStateOfUser(userId);
    }

}
