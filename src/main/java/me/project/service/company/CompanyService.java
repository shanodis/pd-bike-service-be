package me.project.service.company;

import me.project.dtos.request.company.CompanyCreateDTO;
import me.project.dtos.request.company.CompanyUpdateDTO;
import me.project.entitiy.Company;
import me.project.repository.CompanyRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CompanyService implements ICompanyService{

    private final CompanyRepository companyRepository;

    private final static String COMPANY_NOT_FOUND = "Company with id %s not found";

    public Company getCompanyById(UUID CompanyId){
        return companyRepository.findById(CompanyId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,String.format(COMPANY_NOT_FOUND,CompanyId))
        );
    }

    public List<Company> getAllCompanies(){
        return companyRepository.findAll();
    }

    public Company createCompanyIfNotExists(CompanyCreateDTO companyCreateDTO){

        String companyName = companyCreateDTO.getCompanyName();

        String taxNumber = companyCreateDTO.getTaxNumber();

        if(companyRepository.existsByCompanyNameAndTaxNumber(companyName, taxNumber)){
            return companyRepository.getCompanyByCompanyNameAndTaxNumber(companyName,taxNumber);
        }

        Company newCompany = new Company(companyName, taxNumber);

        companyRepository.save(newCompany);
        return newCompany;
    }

    public void updateCompany(UUID CompanyId, CompanyUpdateDTO companyUpdateDto){
        Company oldCompany = companyRepository.findById(CompanyId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,String.format(COMPANY_NOT_FOUND,CompanyId))
        );

        oldCompany.setCompanyName(companyUpdateDto.getCompanyName());
        oldCompany.setTaxNumber(companyUpdateDto.getTaxNumber());

        companyRepository.save(oldCompany);
    }

    public void deleteCompanyById(UUID CompanyId){
        companyRepository.findById(CompanyId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,String.format(COMPANY_NOT_FOUND,CompanyId))
        );

        companyRepository.deleteById(CompanyId);
    }

}
