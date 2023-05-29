package me.project.service.auth;

import lombok.AllArgsConstructor;
import me.project.dtos.request.user.Toggle2FADTO;
import me.project.dtos.response.user.Toggle2FADTOResponse;
import me.project.entitiy.User;
import me.project.repository.UserRepository;
import org.apache.commons.codec.binary.Base32;
import org.jboss.aerogear.security.otp.Totp;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.security.SecureRandom;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TotpService {
    private static final String QR_PREFIX = "https://chart.googleapis.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=";
    private UserRepository userRepository;

    public String generateSecret() {
        Base32 base32 = new Base32();
        byte[] bytes = new byte[10];
        new SecureRandom().nextBytes(bytes);
        return base32.encodeAsString(bytes);
    }

    public String generateQRUrl(User user) {
        return QR_PREFIX + URLEncoder.encode(String.format(
                        "otpauth://totp/%s:%s?secret=%s&issuer=%s",
                        "SpringSecurity",
                        user.getUsername(),
                        user.getSecret2FA(),
                        "SpringSecurity"),
                StandardCharsets.UTF_8);
    }

    public boolean verifyCode(String secret, int code) {
        Totp totp = new Totp(secret);
        return totp.verify(String.valueOf(code));
    }

    public ResponseEntity<?> verify2FA(int totp, Principal principal) {
        Optional<User> optionalUser = userRepository.findByEmail(principal.getName());

        if (!optionalUser.isPresent()) {
            return (ResponseEntity<?>) ResponseEntity.notFound();
        }

        User user = optionalUser.get();

        if (verifyCode(user.getSecret2FA(), totp)) {
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    public ResponseEntity<?> toggle2FA(Principal principal, Toggle2FADTO toggle2FADTO) {
        Optional<User> optionalUser = userRepository.findByEmail(principal.getName());

        if (!optionalUser.isPresent()) {
            return (ResponseEntity<?>) ResponseEntity.notFound();
        }

        boolean is2FAEnabled = toggle2FADTO.getIsUsing2FA();

        User user = optionalUser.get();
        user.setSecret2FA(is2FAEnabled ? generateSecret() : null);
        user.setIsUsing2FA(is2FAEnabled);
        userRepository.save(user);
        String qrUrl = generateQRUrl(user);
        return ResponseEntity.ok(new Toggle2FADTOResponse(is2FAEnabled, qrUrl));
    }
}