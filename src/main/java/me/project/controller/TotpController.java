package me.project.controller;

import lombok.AllArgsConstructor;
import me.project.service.auth.TotpService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@AllArgsConstructor
public class TotpController {
    private TotpService totpService;

    @PatchMapping("/login/2fa/enable")
    public ResponseEntity<?> enable2FA(Principal principal) {
        return totpService.enable2FA(principal);
    }

    @GetMapping("/login/2fa/verify")
    public ResponseEntity<?> verify2FA(@RequestParam int totp, Principal principal) {
        return totpService.verify2FA(totp, principal);
    }
}
