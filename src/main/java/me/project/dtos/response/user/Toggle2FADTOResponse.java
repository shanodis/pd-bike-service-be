package me.project.dtos.response.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class Toggle2FADTOResponse implements Serializable {
    private Boolean isUsing2FA;
    private String qrUrl;
}
