package me.project.auth.jwt;


import me.project.entitiy.User;
import me.project.service.user.IUserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.assertj.core.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Service
@AllArgsConstructor
public class JwtTokenRefresher {
    private final IUserService userService;

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) {

        String authorizationHeader = request.getHeader("Authorization-Refresh");

        if (!Strings.isNullOrEmpty(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.replace("Bearer ", "");
            try {
                //Kidding :)
                String secretKey = "someStringHashToHaveReallyGoodSecurityOverHereSoNoOneWithAmateurSkillsWouldn'tHackThis";

                Jws<Claims> claimsJws = Jwts.parserBuilder()
                        .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                        .build()
                        .parseClaimsJws(token);

                Claims body = claimsJws.getBody();

                UUID userId = body.get("userId", UUID.class);

                User user = userService.getUser(userId);

                String accessToken = Jwts.builder()
                        .setSubject(user.getFirstName() + " " + user.getLastName())
                        .claim("authorities", user.getAuthorities())
                        .claim("userId", userId)
                        .setIssuedAt(new Date())
                        .setExpiration(new Date(System.currentTimeMillis() + 2 * 60 * 1000))
                        .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                        .compact();

                response.addHeader("Authorization", "Bearer " + accessToken);

            } catch (Exception e) {
                if (e.getClass().equals(ExpiredJwtException.class))
                    throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, String.format("Token %s has expired, please login again", token));
                else
                    throw new IllegalStateException(String.format("Token %s cannot be trusted", token));
            }

        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Authorization-Refresh token hasn't been provided with request");
    }

}
