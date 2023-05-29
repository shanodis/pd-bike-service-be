package me.project.auth.formLogin;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import me.project.service.user.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import static me.project.enums.JwtExpire.ACCESS_TOKEN;
import static me.project.enums.JwtExpire.REFRESH_TOKEN;

@AllArgsConstructor
public class FormLoginHelper implements AuthenticationSuccessHandler {
    private final UserService userService;

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
}
