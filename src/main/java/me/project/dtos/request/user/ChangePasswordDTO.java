package me.project.dtos.request.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordDTO implements Serializable {

    @NotNull
    private String OldPassword;

    @NotNull
    private String NewPassword;

    @NotNull
    private String NewPasswordConfirm;
}
