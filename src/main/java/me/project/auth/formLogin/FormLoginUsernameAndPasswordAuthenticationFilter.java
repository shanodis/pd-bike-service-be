package me.project.auth.formLogin;

import me.project.service.user.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
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
@Getter
public class FormLoginUsernameAndPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final boolean postOnly = true;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (this.postOnly && !request.getMethod().equals("POST"))
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());

        String username = this.obtainUsername(request);
        username = username != null ? username : "";
        username = username.trim();

        String password = this.obtainPassword(request);
        password = password != null ? password : "";

        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
        this.setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);

    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        try {
            String key = "someStringHashToHaveReallyGoodSecurityOverHereSoNoOneWithAmateurSkillsWouldn'tHackThis";
            String username = this.obtainUsername(request);

            UUID userId = userService.getUser(username).getUserId();

            String accessToken = Jwts.builder()
                    .setSubject(authResult.getName())
                    .claim("authorities", authResult.getAuthorities())
                    .claim("userId", userId)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN.getAmount()))
                    .signWith(Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8)))
                    .compact();

            String refreshToken = Jwts.builder()
                    .setSubject(authResult.getName())
                    .claim("userId", userId)
                    .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN.getAmount()))
                    .signWith(Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8)))
                    .compact();


            response.addHeader("Authorization", "Bearer " + accessToken);
            response.addHeader("Authorization-Refresh", "Bearer " + refreshToken);

        } catch (Exception e) {
            if (e.equals(new IOException(e.getMessage())))
                throw new IOException(e.getMessage());
            else throw new ServletException(e.getMessage());
        }
    }
}
