package me.project.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomWebAuthenticationDetailsTest {

    @Test
    @DisplayName("Should return the verification code from the request")
    void getVerificationCodeFromRequest() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("verificationCode")).thenReturn("123456");

        CustomWebAuthenticationDetails details = new CustomWebAuthenticationDetails(request);

        assertEquals("123456", details.getVerificationCode());
    }

}