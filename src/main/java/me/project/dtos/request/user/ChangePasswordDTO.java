package me.project.dtos.request.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordDTO implements Serializable {

    @NotNull
    private String oldPassword;

    @NotNull
    private String newPassword;

    @NotNull
    private String newPasswordConfirm;
}
