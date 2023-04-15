package me.project.dtos.request;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class ChangePasswordDTO implements Serializable {
    private final UUID UserId;
    private final String OldPassword;
    private final String NewPassword;
    private final String NewPasswordConfirm;
}
