package me.project.dtos.request.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewPasswordDTO implements Serializable {

    @NotNull
    private String NewPassword;

    @NotNull
    private String NewPasswordConfirm;
}
