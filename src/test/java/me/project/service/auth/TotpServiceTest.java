package me.project.service.auth;

import me.project.dtos.request.user.Toggle2FADTO;
import me.project.dtos.response.user.Toggle2FADTOResponse;
import me.project.entitiy.User;
import me.project.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TotpServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TotpService totpService;

    @Test
    @DisplayName("Should return not found when the user is not found")
    void toggle2FAWhenUserNotFoundReturnsNotFound() {
        String email = "john.doe@example.com";
        Toggle2FADTO toggle2FADTO = new Toggle2FADTO(true);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        ResponseEntity<?> responseEntity = totpService.toggle2FA(() -> email, toggle2FADTO);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should disable 2FA and return null QR URL when 2FA is toggled off")
    void toggle2FAWhenDisabledReturnsNullQRUrl() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setIsUsing2FA(true);
        user.setSecret2FA("testSecret");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        Toggle2FADTO toggle2FADTO = new Toggle2FADTO(false);

        ResponseEntity<?> responseEntity = totpService.toggle2FA(user::getEmail, toggle2FADTO);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Toggle2FADTOResponse toggle2FADTOResponse = (Toggle2FADTOResponse) responseEntity.getBody();
        assertNotNull(toggle2FADTOResponse);
        assertFalse(toggle2FADTOResponse.getIsUsing2FA());
        assertNull(toggle2FADTOResponse.getQrUrl());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Should enable 2FA and return QR URL when 2FA is toggled on")
    void toggle2FAWhenEnabledReturnsQRUrl() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setSecret2FA(null);
        user.setIsUsing2FA(false);

        Toggle2FADTO toggle2FADTO = new Toggle2FADTO(true);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        ResponseEntity<?> responseEntity = totpService.toggle2FA(user::getEmail, toggle2FADTO);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(Boolean.TRUE, user.getIsUsing2FA());
        assertNotNull(user.getSecret2FA());
        assertTrue(responseEntity.getBody() instanceof Toggle2FADTOResponse);
        Toggle2FADTOResponse toggle2FADTOResponse = (Toggle2FADTOResponse) responseEntity.getBody();
        assertTrue(toggle2FADTOResponse.getIsUsing2FA());
        assertNotNull(toggle2FADTOResponse.getQrUrl());
    }
}