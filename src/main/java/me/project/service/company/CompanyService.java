package me.project.service.company;

import me.project.entitiy.User;
import me.project.dtos.request.company.CompanyCreateDTO;
import me.project.dtos.request.company.CompanyUpdateDTO;
import me.project.entitiy.Company;
import me.project.repository.CompanyRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CompanyService implements ICompanyService{

    private final CompanyRepository companyRepository;

    private static final String COMPANY_NOT_FOUND = "Company with id %s not found";

    @Override
    public Company getCompanyByUser(User user) {
        return companyRepository.getCompanyByUser(user);
    }

    public Company getCompanyById(UUID companyId){
        return companyRepository.findById(companyId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,String.format(COMPANY_NOT_FOUND,companyId))
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

        Company newCompany = new Company(companyName, taxNumber, companyCreateDTO.getUser());

        companyRepository.save(newCompany);
        return newCompany;
    }

    @Transactional
    public void updateCompany(UUID companyId, CompanyUpdateDTO companyUpdateDto){
        Company oldCompany = companyRepository.findById(companyId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,String.format(COMPANY_NOT_FOUND,companyId))
        );

        oldCompany.setCompanyName(companyUpdateDto.getCompanyName());
        oldCompany.setTaxNumber(companyUpdateDto.getTaxNumber());

        companyRepository.save(oldCompany);
    }

    public void deleteCompanyById(UUID companyId) throws ResponseStatusException {
        Company company = companyRepository.findById(companyId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,String.format(COMPANY_NOT_FOUND,companyId))
        );

        companyRepository.deleteById(company.getCompanyId());
    }

}
