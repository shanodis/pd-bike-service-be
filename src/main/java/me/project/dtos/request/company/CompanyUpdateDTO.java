package me.project.dtos.request.company;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyUpdateDTO implements Serializable {

    private String companyName;

    private String taxNumber;
}
