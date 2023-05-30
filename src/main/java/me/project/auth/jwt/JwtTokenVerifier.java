package me.project.auth.jwt;

import com.amazonaws.services.iot.model.UnauthorizedException;
import com.amazonaws.services.lexruntime.model.NotAcceptableException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.assertj.core.util.Strings;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class JwtTokenVerifier extends OncePerRequestFilter {
    private final String[] EXCLUDED_PATHS = {"/api/v1/auth/refresh-access"};

    private String getFilerJsonError(HttpServletRequest request, String message) {
        return "{ " +
                "\"error\": \"Unauthorized\", " +
                "\"message\": \"" +
                message +
                "\", " +
                "\"path\": \"" +
                request.getRequestURL() +
                "\"" +
                "} ";
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return Arrays.asList(EXCLUDED_PATHS).contains(path);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");

        if (Strings.isNullOrEmpty(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
        } else {
            String token = authorizationHeader.replace("Bearer ", "");
            try {
                String secretKey = "someStringHashToHaveReallyGoodSecurityOverHereSoNoOneWithAmateurSkillsWouldn'tHackThis";

                Jws<Claims> claimsJws = Jwts.parserBuilder()
                        .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8))).build()
                        .parseClaimsJws(token);
                Claims body = claimsJws.getBody();

                String username = body.getSubject();
                List<Map<String, String>> authorities = (List<Map<String, String>>) body.get("authorities");

                Set<SimpleGrantedAuthority> simpleGrantedAuthorities =
                        authorities.stream()
                                .map(m -> new SimpleGrantedAuthority(m.get("authority")))
                                .collect(Collectors.toSet());

                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        username, null, simpleGrantedAuthorities
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                if (e.getClass().equals(ExpiredJwtException.class)) {
                    String jsonError = getFilerJsonError(request, String.format("Token %s has expired, please login again", token));
                    response.setContentType("application/json");
                    response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
                    response.getWriter().write(jsonError);
                    return;
                }

                throw new UnauthorizedException(String.format("Token %s cannot be trusted", token));
            }

            filterChain.doFilter(request, response);
        }
    }
}
