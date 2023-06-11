package me.project.auth.jwt;

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
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Klasa JwtTokenVerifier jest odpowiedzialna za weryfikację tokenu JWT w żądaniach.
 * Dziedziczy po klasie OncePerRequestFilter, co zapewnia, że filtr zostanie wykonany tylko raz dla każdego żądania.
 */
public class JwtTokenVerifier extends OncePerRequestFilter {
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private static final String[] EXCLUDED_PATHS = {
            "/v2/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            // -- Swagger UI v3 (OpenAPI)
            "/v3/api-docs/**",
            "/swagger-ui/**",
            // -- Rest of endpoints
            "/login/**",
            "/api/v1/auth/**",
            "/api/v1/files/{fileId}/download"
    };

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

    /**
     * Metoda shouldNotFilter sprawdza, czy dla danego żądania nie powinien być stosowany filtr.
     *
     * @param request Obiekt HttpServletRequest reprezentujący żądanie HTTP.
     * @return true, jeśli filtr nie powinien być stosowany dla żądania, w przeciwnym razie false.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return Arrays.stream(EXCLUDED_PATHS).anyMatch(p -> pathMatcher.match(p, path));
    }

    /**
     * Metoda doFilterInternal wykonuje logikę filtrowania dla tokenu JWT.
     * Weryfikuje poprawność tokenu, ustawia uwierzytelnienie użytkownika na podstawie tokenu i przepuszcza żądanie dalej w łańcuchu filtrów.
     *
     * @param request     Obiekt HttpServletRequest reprezentujący żądanie HTTP.
     * @param response    Obiekt HttpServletResponse reprezentujący odpowiedź HTTP.
     * @param filterChain Obiekt FilterChain umożliwiający przepuszczenie żądania dalej w łańcuchu filtrów.
     * @throws ServletException Jeśli wystąpił błąd podczas przetwarzania żądania.
     * @throws IOException      Jeśli wystąpił błąd wejścia-wyjścia podczas przetwarzania żądania.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        if (Strings.isNullOrEmpty(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")) {
            String jsonError = getFilerJsonError(request, "Invalid token. Unauthorized");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(jsonError);
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
                    response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
                    response.setContentType("application/json");
                    response.getWriter().write(jsonError);
                    return;
                }

                String jsonError = getFilerJsonError(request, String.format("Token %s cannot be trusted", token));
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(jsonError);
            }

            filterChain.doFilter(request, response);
        }
    }
}
