package me.project.auth.oauth2;

import me.project.auth.enums.AppUserRole;
import me.project.entitiy.User;
import me.project.service.user.IUserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static me.project.enums.JwtExpire.ACCESS_TOKEN;
import static me.project.enums.JwtExpire.REFRESH_TOKEN;


public class Oauth2SuccessLoginHandler extends SimpleUrlAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final IUserService userService;

    public Oauth2SuccessLoginHandler(IUserService userService) {
        super();
        setUseReferer(true);
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
        User user;

        String[] oauthNames = oauthUser.getName().split(" ", 2);

        if (!userService.existsByEmail(oauthUser.getEmail())) {
            user = userService.createOAuth2User(
                    oauthUser.getEmail(),
                    oauthNames[0].trim(),
                    oauthNames[1].trim(),
                    AppUserRole.CLIENT
            );

            httpServletResponse.setStatus(HttpStatus.CREATED.value());

        } else {
            user = userService.getUser(oauthUser.getEmail());
            httpServletResponse.setStatus(HttpStatus.OK.value());
        }

        try {
            String key = "someStringHashToHaveReallyGoodSecurityOverHereSoNoOneWithAmateurSkillsWouldn'tHackThis";

            String accessToken = Jwts.builder()
                    .setSubject(authentication.getName())
                    .claim("authorities", user.getAuthorities())
                    .claim("userId", user.getUserId())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN.getAmount()))
                    .signWith(Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8)))
                    .compact();

            String refreshToken = Jwts.builder()
                    .setSubject(authentication.getName())
                    .claim("userId", user.getUserId())
                    .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN.getAmount()))
                    .signWith(Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8)))
                    .compact();

            httpServletResponse.setHeader("Authorization", "Bearer " + accessToken);
            httpServletResponse.setHeader("Authorization-Refresh", "Bearer " + refreshToken);

            String targetUrl = UriComponentsBuilder.fromHttpUrl("http://localhost:3000")
                    .queryParam("accessToken", "Bearer " + accessToken)
                    .queryParam("refreshToken", "Bearer " + refreshToken)
                    .queryParam("status", httpServletResponse.getStatus())
                    .build().toUriString();

            getRedirectStrategy().sendRedirect(httpServletRequest, httpServletResponse, targetUrl);

        } catch (Exception e) {

            if (e.equals(new IOException(e.getMessage())))
                throw new IOException(e.getMessage());

            else throw new ServletException(e.getMessage());
        }

    }
}
