package me.project.repository;

import me.project.auth.User;
import me.project.entitiy.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID>, JpaSpecificationExecutor<Company> {

    boolean existsByCompanyNameAndTaxNumber(String companyName, String taxNumber);

    Company getCompanyByCompanyNameAndTaxNumber(String companyName, String taxNumber);

    Company getCompanyByUser(User user);
}