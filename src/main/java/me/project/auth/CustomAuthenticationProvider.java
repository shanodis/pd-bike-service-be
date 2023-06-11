package me.project.auth;

import lombok.AllArgsConstructor;
import me.project.entitiy.User;
import me.project.service.auth.TotpService;
import me.project.service.user.UserService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final TotpService totpService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String verificationCode = ((CustomWebAuthenticationDetails) authentication.getDetails()).getVerificationCode();

        User user = userService.findUserByEmailSilent(authentication.getName().trim());
        String password = authentication.getCredentials().toString();

        if (user == null || !bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        if (Boolean.TRUE.equals(user.getIsUsing2FA()) && (verificationCode == null || !totpService.verifyCode(user.getSecret2FA(), Integer.parseInt(verificationCode)))) {
            throw new AuthenticationServiceException("Invalid 2FA code");
        }

        return new UsernamePasswordAuthenticationToken(user, password, user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(UsernamePasswordAuthenticationToken.class);
    }
}
