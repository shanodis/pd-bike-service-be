package me.project.auth.jwt;


import me.project.entitiy.User;
import me.project.service.user.IUserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
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

import static me.project.enums.JwtExpire.ACCESS_TOKEN;

/**
 * Klasa JwtTokenRefresher jest odpowiedzialna za odświeżanie tokenu JWT.
 * Jest używana do generowania nowego tokenu dostępowego na podstawie tokenu odświeżającego.
 */
@Service
@AllArgsConstructor
public class JwtTokenRefresher {
    private final IUserService userService;

    /**
     * Metoda refreshToken odświeża token JWT na podstawie przekazanego żądania i odpowiedzi HTTP.
     * Wykorzystuje token odświeżający, aby uzyskać nowy token dostępowy dla użytkownika.
     * Aktualizuje nagłówek "Authorization" w odpowiedzi z nowym tokenem dostępowym.
     *
     * @param request  Obiekt HttpServletRequest reprezentujący żądanie HTTP.
     * @param response Obiekt HttpServletResponse reprezentujący odpowiedź HTTP.
     * @throws ResponseStatusException Jeśli wystąpił błąd podczas odświeżania tokenu lub token odświeżający nie został przekazany.
     */
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

                String UID = body.get("userId", String.class);

                UUID userId = UUID.fromString(UID);

                User user = userService.getUser(userId);

                String accessToken = Jwts.builder()
                        .setSubject(user.getFirstName() + " " + user.getLastName())
                        .claim("authorities", user.getAuthorities())
                        .claim("userId", userId)
                        .setIssuedAt(new Date())
                        .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN.getAmount()))
                        .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                        .compact();

                response.addHeader("Authorization", "Bearer " + accessToken);
                response.setStatus(HttpStatus.OK.value());

            } catch (Exception e) {

                if (e.getClass().equals(ExpiredJwtException.class))
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, String.format("Token %s has expired, please login again", token));

                if (e.getClass().equals(SignatureException.class))
                    throw e;

                else
                    throw new IllegalStateException(String.format("Token %s cannot be trusted", token));
            }

        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Authorization-Refresh token hasn't been provided with request");
    }

}
