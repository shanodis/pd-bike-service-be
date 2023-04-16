package me.project.controller;

import me.project.dtos.request.company.CompanyCreateDTO;
import me.project.dtos.request.company.CompanyUpdateDTO;
import me.project.entitiy.Company;
import me.project.service.company.ICompanyService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/companies")
@AllArgsConstructor
public class CompanyController {
    private final ICompanyService companyService;

    @GetMapping
    public List<Company> getAllCompanies() {
        return companyService.getAllCompanies();
    }

    @GetMapping("{id}")
    public Company getCompanyById(@PathVariable("id") UUID CompanyId) {
        return companyService.getCompanyById(CompanyId);
    }

    @PostMapping
    public Company createCompanyIfNotExists(@RequestBody CompanyCreateDTO companyCreateDTO) {
        return companyService.createCompanyIfNotExists(companyCreateDTO);
    }

    @PutMapping("{id}")
    public void updateCompany(@PathVariable("id") UUID CompanyId,@RequestBody CompanyUpdateDTO companyUpdateDto) {
        companyService.updateCompany(CompanyId, companyUpdateDto);
    }

    @DeleteMapping("{id}")
    public void deleteCompanyById(@PathVariable("id") UUID CompanyId) {
        companyService.deleteCompanyById(CompanyId);
    }
}
