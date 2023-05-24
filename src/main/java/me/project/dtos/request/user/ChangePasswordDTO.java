package me.project.dtos.request.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordDTO implements Serializable {
    private String OldPassword;
    private String NewPassword;
    private String NewPasswordConfirm;
}
