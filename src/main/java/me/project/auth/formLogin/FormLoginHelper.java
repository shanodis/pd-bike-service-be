package me.project.auth.formLogin;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import me.project.service.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static me.project.enums.JwtExpire.ACCESS_TOKEN;
import static me.project.enums.JwtExpire.REFRESH_TOKEN;

/**
 * Klasa FormLoginHelper implementuje interfejsy AuthenticationSuccessHandler i AuthenticationFailureHandler.
 * Służy do obsługi zdarzeń logowania - sukcesu i niepowodzenia uwierzytelnienia.
 */
@AllArgsConstructor
public class FormLoginHelper implements AuthenticationSuccessHandler, AuthenticationFailureHandler {
    private final UserService userService;
    private ObjectMapper objectMapper;

    /**
     * Metoda onAuthenticationSuccess jest wywoływana, gdy uwierzytelnienie użytkownika zakończyło się sukcesem.
     * Tworzy nowe tokeny dostępowe (JWT) dla użytkownika i dodaje je jako nagłówki odpowiedzi HTTP.
     *
     * @param request        Obiekt HttpServletRequest reprezentujący żądanie HTTP.
     * @param response       Obiekt HttpServletResponse reprezentujący odpowiedź HTTP.
     * @param authentication Obiekt Authentication reprezentujący zakończone uwierzytelnienie.
     * @throws IOException      Jeśli wystąpił błąd podczas zapisu do strumienia odpowiedzi.
     * @throws ServletException Jeśli wystąpił błąd podczas przetwarzania żądania.
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        try {
            String key = "someStringHashToHaveReallyGoodSecurityOverHereSoNoOneWithAmateurSkillsWouldn'tHackThis";
            String username = authentication.getName();
            UUID userId = userService.getUser(username).getUserId();

            String accessToken = Jwts.builder()
                    .setSubject(authentication.getName())
                    .claim("authorities", authentication.getAuthorities())
                    .claim("userId", userId)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN.getAmount()))
                    .signWith(Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8)))
                    .compact();

            String refreshToken = Jwts.builder()
                    .setSubject(authentication.getName())
                    .claim("userId", userId)
                    .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN.getAmount()))
                    .signWith(Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8)))
                    .compact();


            response.addHeader("Authorization", "Bearer " + accessToken);
            response.addHeader("Authorization-Refresh", "Bearer " + refreshToken);

        } catch (Exception e) {
            if (e.equals(new IOException(e.getMessage()))) {
                throw new IOException(e.getMessage());
            }
            throw new ServletException(e.getMessage());
        }
    }

    /**
     * Metoda onAuthenticationFailure jest wywoływana, gdy uwierzytelnienie użytkownika zakończyło się niepowodzeniem.
     * Ustawia odpowiedni status odpowiedzi HTTP i zwraca komunikat o błędzie w formacie JSON.
     *
     * @param request   Obiekt HttpServletRequest reprezentujący żądanie HTTP.
     * @param response  Obiekt HttpServletResponse reprezentujący odpowiedź HTTP.
     * @param exception Wyjątek AuthenticationException reprezentujący błąd uwierzytelnienia.
     * @throws IOException Jeśli wystąpił błąd podczas zapisu do strumienia odpowiedzi.
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        Map<String, Object> data = new HashMap<>();
        data.put("timestamp", Calendar.getInstance().getTime());
        data.put("exception", exception.getMessage());

        response.getOutputStream().println(objectMapper.writeValueAsString(data));
    }
}
