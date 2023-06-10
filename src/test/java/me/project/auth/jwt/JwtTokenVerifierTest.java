package me.project.auth.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JwtTokenVerifierTest {

    private JwtTokenVerifier jwtTokenVerifier;

    @BeforeEach
    void setUp() {
        jwtTokenVerifier = new JwtTokenVerifier();
    }


    @SneakyThrows
    @Test
    @DisplayName("Should return unauthorized status and error message when token cannot be trusted")
    void doFilterInternalWhenTokenCannotBeTrusted() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        String token = "invalidToken";
        String authorizationHeader = "Bearer " + token;

        when(request.getHeader("Authorization")).thenReturn(authorizationHeader);
        when(response.getWriter()).thenReturn(mock(PrintWriter.class));

        try {
            jwtTokenVerifier.doFilterInternal(request, response, filterChain);
            verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } catch (ServletException | IOException e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @SneakyThrows
    @Test
    @DisplayName("Should call filterChain.doFilter when authorization header is missing or doesn't start with 'Bearer '")
    void doFilterInternalWhenAuthorizationHeaderIsMissingOrInvalid() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(request.getHeader("Authorization")).thenReturn(null);

        assertDoesNotThrow(() -> jwtTokenVerifier.doFilterInternal(request, response, filterChain));

        verify(filterChain, times(1)).doFilter(request, response);

        when(request.getHeader("Authorization")).thenReturn("InvalidToken");

        assertDoesNotThrow(() -> jwtTokenVerifier.doFilterInternal(request, response, filterChain));

        verify(filterChain, times(2)).doFilter(request, response);

        when(request.getHeader("Authorization")).thenReturn("Bearer ValidToken");
        when(response.getWriter()).thenReturn(mock(PrintWriter.class));

        assertDoesNotThrow(() -> jwtTokenVerifier.doFilterInternal(request, response, filterChain));

        verify(filterChain, times(3)).doFilter(request, response);
    }

    @SneakyThrows
    @Test
    @DisplayName("Should set authentication in security context when token is valid")
    void doFilterInternalWhenTokenIsValid() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        String token = "validToken";
        String authorizationHeader = "Bearer " + token;
        String username = "testUser";
        List<Map<String, String>> authorities = new ArrayList<>();
        Map<String, String> authority = new HashMap<>();
        authority.put("authority", "ROLE_USER");
        authorities.add(authority);

        Claims claims = Jwts.claims();
        claims.setSubject(username);
        claims.put("authorities", authorities);

        when(request.getHeader("Authorization")).thenReturn(authorizationHeader);
        when(response.getWriter()).thenReturn(mock(PrintWriter.class));

        try {
            jwtTokenVerifier.doFilterInternal(request, response, filterChain);
        } catch (ServletException | IOException e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Should return a JSON error with Unauthorized status and custom message")
    void getFilerJsonErrorReturnsUnauthorizedJsonErrorWithCustomMessage() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        String message = "Custom error message";

        String expectedJsonError = "{ " +
                "\"error\": \"Unauthorized\", " +
                "\"message\": \"" +
                message +
                "\", " +
                "\"path\": \"" +
                request.getRequestURL() +
                "\"" +
                "} ";

        String actualJsonError = ReflectionTestUtils.invokeMethod(jwtTokenVerifier, "getFilerJsonError", request, message);

        assert actualJsonError != null;
        assertFalse(actualJsonError.isEmpty());
        assertTrue(actualJsonError.contains("\"error\": \"Unauthorized\""));
        assertTrue(actualJsonError.contains("\"message\": \"" + message + "\""));
        assertTrue(actualJsonError.contains("\"path\": \"" + request.getRequestURL() + "\""));
        assertEquals(actualJsonError, expectedJsonError);
    }

    @Test
    @DisplayName("Should not filter when the request URI is in the excluded paths")
    void shouldNotFilterWhenRequestUriIsInExcludedPaths() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/v1/auth/refresh-access");

        boolean shouldNotFilter = jwtTokenVerifier.shouldNotFilter(request);

        assertTrue(shouldNotFilter);
    }

    @Test
    @DisplayName("Should filter when the request URI is not in the excluded paths")
    void shouldFilterWhenRequestUriIsNotInExcludedPaths() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/v1/users");

        boolean shouldFilter = jwtTokenVerifier.shouldNotFilter(request);

        assertFalse(shouldFilter);
    }
}