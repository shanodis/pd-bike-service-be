package me.project.auth;

import lombok.AllArgsConstructor;
import me.project.entitiy.User;
import me.project.service.auth.TotpService;
import me.project.service.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@AllArgsConstructor
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final TotpService totpService;

    @Override
    public Authentication authenticate(Authentication authentication) throws ResponseStatusException {
        String verificationCode = ((CustomWebAuthenticationDetails) authentication.getDetails()).getVerificationCode();

        User user = userService.findUserByEmailSilent(authentication.getName().trim());
        String password = authentication.getCredentials().toString();

        if (user == null || !bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }

        if (user.getIsUsing2FA() && (verificationCode == null || !totpService.verifyCode(user.getSecret2FA(), Integer.parseInt(verificationCode)))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid 2FA code");
        }

        return new UsernamePasswordAuthenticationToken(user, password);
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(UsernamePasswordAuthenticationToken.class);
    }
}
