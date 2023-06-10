package me.project.auth.formLogin;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import me.project.auth.enums.AppUserRole;
import me.project.entitiy.User;
import me.project.service.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FormLoginHelperTest {

    @Mock
    private UserService userService;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private FormLoginHelper formLoginHelper;


    @Test
    @DisplayName("Should throw ServletException when a non-IO error occurs during token generation")
    void onAuthenticationSuccessThrowsServletExceptionOnError() {
        try {
            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            Authentication authentication = mock(Authentication.class);
            ServletOutputStream outputStream = mock(ServletOutputStream.class);

            String username = "testUser";

            when(userService.getUser(username)).thenReturn(new User());

            when(response.getOutputStream()).thenReturn(outputStream);
            when(objectMapper.writeValueAsString(any())).thenReturn("testJson");

            doThrow(new RuntimeException("Test Exception")).when(userService).getUser(username);

            formLoginHelper.onAuthenticationSuccess(request, response, authentication);

            fail("Should have thrown ServletException");

        } catch (IOException | ServletException e) {
            // expected exception
        }
    }

    @SneakyThrows
    @Test
    @DisplayName("Should generate and set access and refresh tokens in headers on successful authentication")
    void onAuthenticationSuccessGeneratesAndSetsTokensInHeaders() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        Authentication authentication = mock(Authentication.class);
        ServletOutputStream outputStream = mock(ServletOutputStream.class);

        String username = "testUser";
        UUID userId = UUID.randomUUID();
        String accessToken = "testAccessToken";
        String refreshToken = "testRefreshToken";

        User user = new User();
        user.setFirstName("testUser");
        user.setLastName("Test");
        user.setEmail("test@example.com");
        user.setAppUserRole(AppUserRole.CLIENT);
        user.setPassword("testPassword");
        user.setEnabled(true);
        user.setIsUsing2FA(true);

        when(authentication.getName()).thenReturn(username);
        when(userService.getUser(username)).thenReturn(user);

        try {
            formLoginHelper.onAuthenticationSuccess(request, response, authentication);
            verify(response, times(1)).addHeader(eq("Authorization"), anyString());
            verify(response, times(1)).addHeader(eq("Authorization-Refresh"), anyString());
        } catch (IOException | ServletException e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Should throw IOException when an IO error occurs during token generation")
    void onAuthenticationSuccessThrowsIOExceptionOnError() {
        try {
            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            Authentication authentication = mock(Authentication.class);
            ServletOutputStream outputStream = mock(ServletOutputStream.class);
            PrintWriter writer = mock(PrintWriter.class);

            String username = "testUser";
            UUID userId = UUID.randomUUID();
            String accessToken = "testAccessToken";
            String refreshToken = "testRefreshToken";
            String key = "someStringHashToHaveReallyGoodSecurityOverHereSoNoOneWithAmateurSkillsWouldn'tHackThis";

            User user = new User();
            user.setFirstName("testUser");
            user.setLastName("Test");
            user.setEmail("test@example.com");
            user.setAppUserRole(AppUserRole.CLIENT);
            user.setPassword("testPassword");

            when(authentication.getName()).thenReturn(username);
            when(userService.getUser(username)).thenReturn(user);

            formLoginHelper.onAuthenticationSuccess(request, response, authentication);

        } catch (IOException | ServletException e) {
            fail("Unexpected exception was thrown: " + e.getMessage());
        }
    }

    @SneakyThrows
    @Test
    @DisplayName("Should set the response status to UNAUTHORIZED and return an error message when authentication fails")
    void onAuthenticationFailureSetsResponseStatusAndReturnsErrorMessage() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        AuthenticationException exception = mock(AuthenticationException.class);

        Map<String, Object> data = new HashMap<>();
        data.put("timestamp", Calendar.getInstance().getTime());
        data.put("exception", exception.getMessage());

        when(response.getOutputStream()).thenReturn(mock(ServletOutputStream.class));

        try {
            formLoginHelper.onAuthenticationFailure(request, response, exception);
        } catch (IOException e) {
            fail("IOException should not be thrown");
        }

        verify(response, times(1)).setStatus(HttpStatus.UNAUTHORIZED.value());
        try {
            verify(response.getOutputStream(), times(1)).println(objectMapper.writeValueAsString(data));
        } catch (IOException e) {
            fail("IOException should not be thrown");
        }
    }
}