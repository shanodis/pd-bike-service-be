package me.project.dtos.request.company;

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
    private String companyName;

    @NotNull
    private String taxNumber;
}
