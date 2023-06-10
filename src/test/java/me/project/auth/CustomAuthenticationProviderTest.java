package me.project.auth;

import me.project.entitiy.User;
import me.project.service.auth.TotpService;
import me.project.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomAuthenticationProviderTest {

    @Mock
    private UserService userService;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private TotpService totpService;
    @Mock
    private Authentication authentication;
    @Mock
    private CustomWebAuthenticationDetails details;

    private CustomAuthenticationProvider customAuthenticationProvider;

    private User mockedUser;

    @BeforeEach
    void setUp() {
        customAuthenticationProvider = new CustomAuthenticationProvider(userService, bCryptPasswordEncoder, totpService);
        mockedUser = new User();
        mockedUser.setUserId(UUID.randomUUID());
        mockedUser.setFirstName("John");
        mockedUser.setLastName("Doe");
        mockedUser.setEmail("john.doe@example.com");
        mockedUser.setPassword("password");
        mockedUser.setIsUsing2FA(true);
        mockedUser.setSecret2FA("secret");
    }


    @Test
    public void testAuthenticate() {
        when(details.getVerificationCode()).thenReturn("123456");
        when(authentication.getDetails()).thenReturn(details);
        when(authentication.getName()).thenReturn("john.doe@example.com");
        when(authentication.getCredentials()).thenReturn("password");

        when(userService.findUserByEmailSilent("john.doe@example.com")).thenReturn(mockedUser);

        when(bCryptPasswordEncoder.matches("password", "password")).thenReturn(true);

        when(totpService.verifyCode("secret", 123456)).thenReturn(true);

        // Create an instance of the class that contains the authenticate method
        CustomAuthenticationProvider customAuthenticationProvider = new CustomAuthenticationProvider(userService, bCryptPasswordEncoder, totpService);

        // Call the authenticate method
        Authentication result = customAuthenticationProvider.authenticate(authentication);

        // Verify that the result is not null
        assertNotNull(result);

        // Verify that the result is an instance of UsernamePasswordAuthenticationToken
        assertTrue(result instanceof UsernamePasswordAuthenticationToken);

        // Verify that the result contains the expected data
        User user = (User) result.getPrincipal();
        assertEquals(mockedUser.getUserId(), user.getUserId());
        assertEquals(mockedUser.getFirstName(), user.getFirstName());
        assertEquals(mockedUser.getLastName(), user.getLastName());
        assertEquals(mockedUser.getEmail(), user.getEmail());
        assertEquals(mockedUser.getPassword(), user.getPassword());
        assertEquals(Boolean.TRUE, user.getIsUsing2FA());
        assertEquals(mockedUser.getSecret2FA(), user.getSecret2FA());
        assertEquals("password", result.getCredentials());

        // Call the authenticate method with an invalid username
        when(userService.findUserByEmailSilent("invalid@example.com")).thenReturn(null);
        when(authentication.getName()).thenReturn("invalid@example.com");

        try {
            customAuthenticationProvider.authenticate(authentication);
            fail("Expected BadCredentialsException was not thrown");
        } catch (BadCredentialsException e) {
            assertEquals("Invalid username or password", e.getMessage());
        }

        // Call the authenticate method with an invalid 2FA code
        when(userService.findUserByEmailSilent(anyString())).thenReturn(mockedUser);
        when(bCryptPasswordEncoder.matches("password", "password")).thenReturn(true);
        when(totpService.verifyCode("secret", 123456)).thenReturn(false);

        try {
            customAuthenticationProvider.authenticate(authentication);
            fail("Expected AuthenticationServiceException was not thrown");
        } catch (AuthenticationServiceException e) {
            assertEquals("Invalid 2FA code", e.getMessage());
        }
    }

    @Test
    @DisplayName("Should return false when the class is not UsernamePasswordAuthenticationToken")
    void supportsWhenClassIsNotUsernamePasswordAuthenticationToken() {
        Class<?> aClass = Authentication.class;
        assertFalse(customAuthenticationProvider.supports(aClass));
    }

    @Test
    @DisplayName("Should return true when the class is UsernamePasswordAuthenticationToken")
    void supportsWhenClassIsUsernamePasswordAuthenticationToken() {
        Class<?> aClass = UsernamePasswordAuthenticationToken.class;
        boolean result = customAuthenticationProvider.supports(aClass);
        assertTrue(result);
    }
}