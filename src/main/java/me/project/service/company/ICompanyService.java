package me.project.service.company;

import me.project.auth.User;
import me.project.dtos.request.company.CompanyCreateDTO;
import me.project.dtos.request.company.CompanyUpdateDTO;
import me.project.entitiy.Company;

import java.util.List;
import java.util.UUID;

public interface ICompanyService {

    Company getCompanyByUser(User user);

    Company getCompanyById(UUID CompanyId);

    List<Company> getAllCompanies();

    Company createCompanyIfNotExists(CompanyCreateDTO companyCreateDTO);

    void updateCompany(UUID CompanyId, CompanyUpdateDTO companyUpdateDto);

    void deleteCompanyById(UUID CompanyId);

}
