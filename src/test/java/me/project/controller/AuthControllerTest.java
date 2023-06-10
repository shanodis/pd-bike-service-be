package me.project.controller;

import me.project.auth.jwt.JwtTokenRefresher;
import me.project.dtos.request.user.ChangePasswordDTO;
import me.project.dtos.request.user.CustomerRegisterDTO;
import me.project.dtos.request.user.NewPasswordDTO;
import me.project.service.user.IUserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private IUserService iUserService;

    @Mock
    private JwtTokenRefresher tokenRefresher;

    @InjectMocks
    private AuthController authController;



@Test
@DisplayName("Should change the state of the user when the user ID is valid")
void changeStateOfUserWhenUserIdIsValid() {UUID userId = UUID.randomUUID();
        doNothing().when(iUserService).changeStateOfUser(userId);

        authController.changeStateOfUser(userId);

        verify(iUserService, times(1)).changeStateOfUser(userId);
    }

@Test
@DisplayName("Should throw an exception when the user ID is not found")
void changeStateOfUserWhenUserIdNotFoundThenThrowException() {UUID userId = UUID.randomUUID();
        doThrow(new IllegalArgumentException("User not found")).when(iUserService).changeStateOfUser(userId);

        assertThrows(IllegalArgumentException.class, () -> authController.changeStateOfUser(userId));

        verify(iUserService, times(1)).changeStateOfUser(userId);
    }

@Test
    @DisplayName("Should set password and activate account when the user ID is valid")
    void setPasswordAndActivateAccountWhenUserIdIsValid() {
        UUID userId = UUID.randomUUID();
        NewPasswordDTO newPasswordDTO = new NewPasswordDTO("newPassword", "newPassword");

        doNothing().when(iUserService).activateAndSetPassword(userId, newPasswordDTO);

        authController.setPasswordAndActivateAccount(userId, newPasswordDTO);

        verify(iUserService, times(1)).activateAndSetPassword(userId, newPasswordDTO);
    }@Test
    @DisplayName("Should throw an exception when the new password is invalid")
    void setPasswordAndActivateAccountWhenNewPasswordIsInvalidThenThrowException() {// create a new instance of NewPasswordDTO with invalid password
        NewPasswordDTO newPasswordDTO = new NewPasswordDTO("123", "123");

        // mock the iUserService.activateAndSetPassword method to throw an exception
        doThrow(IllegalArgumentException.class)
                .when(iUserService)
                .activateAndSetPassword(any(UUID.class), eq(newPasswordDTO));

        // assert that an exception is thrown when setPasswordAndActivateAccount is called with invalid password
        assertThrows(IllegalArgumentException.class, () -> authController.setPasswordAndActivateAccount(UUID.randomUUID(), newPasswordDTO));

        // verify that iUserService.activateAndSetPassword is called exactly once with the correct arguments
        verify(iUserService, times(1)).activateAndSetPassword(any(UUID.class), eq(newPasswordDTO));
    }@Test
    @DisplayName("Should throw an exception when the old password is incorrect")
    void changeUserPasswordWhenOldPasswordIncorrectThenThrowException() {
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO(
                "incorrectOldPassword", "newPassword", "newPassword");

        UUID userId = UUID.randomUUID();

        doThrow(new IllegalArgumentException("Incorrect old password"))
                .when(iUserService).changeUserPassword(userId, changePasswordDTO);

        assertThrows(IllegalArgumentException.class, () -> {
            authController.changeUserPassword(userId, changePasswordDTO);
        });

        verify(iUserService, times(1)).changeUserPassword(userId, changePasswordDTO);
    }@Test
    @DisplayName("Should change the user password when the provided data is valid")
    void changeUserPasswordWhenDataIsValid() {
        UUID userId = UUID.randomUUID();
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO(
                "oldPassword", "newPassword", "newPassword");

        authController.changeUserPassword(userId, changePasswordDTO);

        verify(iUserService, times(1)).changeUserPassword(userId, changePasswordDTO);
        assertNotNull(changePasswordDTO.getOldPassword());
        assertNotNull(changePasswordDTO.getNewPassword());
        assertNotNull(changePasswordDTO.getNewPasswordConfirm());
    }@Test
    @DisplayName("Should reset the password when the email is valid")
    void resetPasswordWhenEmailIsValid() {
        String email = "test@example.com";
        doNothing().when(iUserService).resetPassword(email);

        authController.resetPassword(email);

        verify(iUserService, times(1)).resetPassword(email);
    }@Test
    @DisplayName("Should create an employee with valid input data")
    void createEmployeeWithValidInput() {
        CustomerRegisterDTO employeeRegisterDTO = new CustomerRegisterDTO(
                "test@example.com", "John", "Doe", "+1", "1234567890", "Note");

        authController.createEmployee(employeeRegisterDTO);

        verify(iUserService, times(1)).createEmployee(employeeRegisterDTO);
    }@Test
    @DisplayName("Should register a new customer with valid input")
    void registerCustomerWithValidInput() {
        CustomerRegisterDTO customerRegisterDTO = new CustomerRegisterDTO(
                "test@example.com", "John", "Doe", "+1", "1234567890", "Test note");

        authController.registerCustomer(customerRegisterDTO);

        verify(iUserService, times(1)).registerCustomer(customerRegisterDTO);
    }@Test
    @DisplayName("Should refresh the access token successfully")
    void refreshAccessTokenSuccessfully() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        doNothing().when(tokenRefresher).refreshToken(request, response);

        authController.refreshAccessToken(request, response);

        verify(tokenRefresher, times(1)).refreshToken(request, response);
    }}