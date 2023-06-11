package me.project.controller;

import me.project.auth.jwt.JwtTokenRefresher;
import me.project.dtos.request.user.ChangePasswordDTO;
import me.project.dtos.request.user.CustomerRegisterDTO;
import me.project.dtos.request.user.NewPasswordDTO;
import me.project.entitiy.User;
import me.project.service.user.IUserService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/auth")
@AllArgsConstructor
public class AuthController {
    private final IUserService iUserService;
    private final JwtTokenRefresher tokenRefresher;

    @GetMapping("refresh-access")
    public void refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        tokenRefresher.refreshToken(request, response);
    }

    @Secured({"ROLE_ADMIN", "ROLE_CLIENT"})
    @PostMapping("register")
    public User registerCustomer(@RequestBody CustomerRegisterDTO customerRegisterDTO) {
        return iUserService.registerCustomer(customerRegisterDTO);
    }

    @Secured({"ROLE_ADMIN"})
    @PostMapping("employee-create")
    public User createEmployee(@RequestBody CustomerRegisterDTO employeeRegisterDTO) {
        return iUserService.createEmployee(employeeRegisterDTO);
    }

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_CLIENT"})
    @PatchMapping("/reset-password")
    public void resetPassword(@RequestBody String email) {
        iUserService.resetPassword(email);
    }

    @Secured({"ROLE_ADMIN"})
    @PutMapping("{id}/password")
    public void changeUserPassword(@PathVariable("id") UUID userId, @RequestBody ChangePasswordDTO changePasswordDTO) {
        iUserService.changeUserPassword(userId, changePasswordDTO);
    }

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_CLIENT"})
    @PatchMapping("{id}/password")
    public void setPasswordAndActivateAccount(@PathVariable("id") UUID userId, @RequestBody NewPasswordDTO newPasswordDTO) {
        iUserService.activateAndSetPassword(userId, newPasswordDTO);
    }

    @Secured({"ROLE_ADMIN"})
    @PatchMapping("{id}/change-user-state")
    public void changeStateOfUser(@PathVariable("id") UUID userId) {
        iUserService.changeStateOfUser(userId);
    }

}
