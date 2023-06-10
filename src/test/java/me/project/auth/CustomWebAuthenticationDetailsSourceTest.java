package me.project.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomWebAuthenticationDetailsSourceTest {

    @Mock
    private CustomWebAuthenticationDetailsSource customWebAuthenticationDetailsSource;

    @Test
    @DisplayName("Should build custom web authentication details from the given HTTP servlet request")
    void buildDetailsFromHttpServletRequest() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("verificationCode")).thenReturn("123456");

        CustomWebAuthenticationDetails customWebAuthenticationDetails = new CustomWebAuthenticationDetails(request);

        CustomWebAuthenticationDetailsSource customWebAuthenticationDetailsSource = new CustomWebAuthenticationDetailsSource();
        CustomWebAuthenticationDetails result = customWebAuthenticationDetailsSource.buildDetails(request);

        assertEquals(customWebAuthenticationDetails.getRemoteAddress(), result.getRemoteAddress());
        assertEquals(customWebAuthenticationDetails.getSessionId(), result.getSessionId());
        assertEquals(customWebAuthenticationDetails.getVerificationCode(), result.getVerificationCode());
    }

}