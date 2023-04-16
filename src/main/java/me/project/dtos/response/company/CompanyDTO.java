package me.project.dtos.response.company;

import me.project.entitiy.Company;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
public class CompanyDTO implements Serializable {
    private final UUID companyId;
    private final String companyName;
    private final String taxNumber;

    public static CompanyDTO convertFromEntity(Company company) {
        return new CompanyDTO(
                company.getCompanyId(),
                company.getCompanyName(),
                company.getTaxNumber()
        );
    }
}
