package me.project.dtos.request.company;

import me.project.entitiy.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyCreateDTO implements Serializable {

    @NotNull
    private User user;

    @NotNull
    private String companyName;

    @NotNull
    private String taxNumber;
}
