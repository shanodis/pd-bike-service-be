package me.project.auth.oauth2;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class OAuth2FailureHandler extends SimpleUrlAuthenticationSuccessHandler implements AuthenticationFailureHandler {

    public OAuth2FailureHandler() {
        super();
        setUseReferer(true);
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        setUseReferer(true);

        httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());

        String targetUrl = UriComponentsBuilder.fromHttpUrl(httpServletRequest.getHeader("Referer"))
                .queryParam("status", httpServletResponse.getStatus())
                .build().toUriString();

        getRedirectStrategy().sendRedirect(httpServletRequest, httpServletResponse, targetUrl);
    }
}
