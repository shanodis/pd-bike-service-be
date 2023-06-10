package me.project.service.company;

import me.project.dtos.request.company.CompanyCreateDTO;
import me.project.dtos.request.company.CompanyUpdateDTO;
import me.project.entitiy.Company;
import me.project.entitiy.User;
import me.project.repository.CompanyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private CompanyService companyService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(UUID.randomUUID());
    }

    @Test
    @DisplayName("Should return null when no company is associated with the given user")
    void getCompanyByUserReturnsNullWhenNoCompanyAssociated() {
        when(companyRepository.getCompanyByUser(user)).thenReturn(null);

        Company result = companyService.getCompanyByUser(user);

        assertEquals(null, result);
        verify(companyRepository, times(1)).getCompanyByUser(user);
    }

    @Test
    @DisplayName("Should return the company associated with the given user")
    void getCompanyByUserReturnsAssociatedCompany() {
        Company company = new Company();
        company.setCompanyId(UUID.randomUUID());
        company.setCompanyName("Test Company");
        company.setTaxNumber("1234567890");
        company.setUser(user);

        when(companyRepository.getCompanyByUser(user)).thenReturn(company);

        Company result = companyService.getCompanyByUser(user);

        assertEquals(company, result);
        verify(companyRepository, times(1)).getCompanyByUser(user);
    }

    @Test
    @DisplayName("Should return the company when the company ID is valid")
    void getCompanyByIdWhenCompanyIdIsValid() {
        UUID companyId = UUID.randomUUID();
        Company company = new Company("Test Company", "1234567890", user);
        company.setCompanyId(companyId);

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));

        Company result = companyService.getCompanyById(companyId);

        assertEquals(company, result);
        verify(companyRepository, times(1)).findById(companyId);
    }

    @Test
    @DisplayName(
            "Should throw a ResponseStatusException with HttpStatus.NOT_FOUND when the company ID is not found")
    void getCompanyByIdWhenCompanyIdIsNotFoundThenThrowResponseStatusException() {
        UUID companyId = UUID.randomUUID();
        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> {
                            companyService.getCompanyById(companyId);
                        });

        assertEquals(exception.getStatus(), HttpStatus.NOT_FOUND);
        verify(companyRepository, times(1)).findById(companyId);
    }

    @Test
    @DisplayName("Should return all companies")
    void getAllCompanies() {
        Company company1 = new Company(UUID.randomUUID(), "Company 1", "123456", user);
        Company company2 = new Company(UUID.randomUUID(), "Company 2", "789012", user);
        Company company3 = new Company(UUID.randomUUID(), "Company 3", "345678", user);
        when(companyRepository.findAll()).thenReturn(List.of(company1, company2, company3));

        List<Company> companies = companyService.getAllCompanies();

        assertEquals(3, companies.size());
        assertEquals(company1, companies.get(0));
        assertEquals(company2, companies.get(1));
        assertEquals(company3, companies.get(2));

        verify(companyRepository, times(1)).findAll();
    }

    @Test
    @DisplayName(
            "Should return an existing company when the company name and tax number already exist")
    void createCompanyIfNotExistsWhenCompanyNameAndTaxNumberExist() {
        String companyName = "Test Company";
        String taxNumber = "1234567890";

        Company existingCompany = new Company(UUID.randomUUID(), companyName, taxNumber, user);

        CompanyCreateDTO companyCreateDTO = new CompanyCreateDTO(user, companyName, taxNumber);

        when(companyRepository.existsByCompanyNameAndTaxNumber(companyName, taxNumber))
                .thenReturn(true);
        when(companyRepository.getCompanyByCompanyNameAndTaxNumber(companyName, taxNumber))
                .thenReturn(existingCompany);

        Company result = companyService.createCompanyIfNotExists(companyCreateDTO);

        verify(companyRepository, times(1)).existsByCompanyNameAndTaxNumber(companyName, taxNumber);
        verify(companyRepository, times(1))
                .getCompanyByCompanyNameAndTaxNumber(companyName, taxNumber);
        verify(companyRepository, times(0)).save(any(Company.class));

        assertEquals(existingCompany, result);
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    @DisplayName(
            "Should create and return a new company when the company name and tax number do not exist")
    void createCompanyIfNotExistsWhenCompanyNameAndTaxNumberDoNotExist() {
        CompanyCreateDTO companyCreateDTO =
                new CompanyCreateDTO(user, "Test Company", "1234567890");

        when(companyRepository.existsByCompanyNameAndTaxNumber(
                companyCreateDTO.getCompanyName(), companyCreateDTO.getTaxNumber()))
                .thenReturn(false);

        Company newCompany =
                new Company(
                        UUID.randomUUID(),
                        companyCreateDTO.getCompanyName(),
                        companyCreateDTO.getTaxNumber(),
                        user);

        when(companyRepository.save(any(Company.class))).thenReturn(newCompany);

        Company result = companyService.createCompanyIfNotExists(companyCreateDTO);

        verify(companyRepository, times(1))
                .existsByCompanyNameAndTaxNumber(
                        companyCreateDTO.getCompanyName(), companyCreateDTO.getTaxNumber());
        verify(companyRepository, times(1)).save(any(Company.class));

        assertEquals(newCompany.getTaxNumber(), result.getTaxNumber());
        assertEquals(newCompany.getCompanyName(), result.getCompanyName());
        assertEquals(newCompany.getUser().getEmail(), result.getUser().getEmail());
    }

    @Test
    @DisplayName("Should throw a ResponseStatusException when the CompanyId is not found")
    void updateCompanyWhenCompanyIdNotFoundThenThrowException() {
        UUID companyId = UUID.randomUUID();
        CompanyUpdateDTO companyUpdateDTO =
                new CompanyUpdateDTO("New Company Name", "New Tax Number");

        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

        assertThrows(
                ResponseStatusException.class,
                () -> companyService.updateCompany(companyId, companyUpdateDTO));

        verify(companyRepository, times(1)).findById(companyId);
        verify(companyRepository, never()).save(any(Company.class));
    }

    @Test
    @DisplayName("Should update the company with the given CompanyId and CompanyUpdateDTO")
    void updateCompanyWithGivenIdAndDto() {
        UUID companyId = UUID.randomUUID();
        CompanyUpdateDTO companyUpdateDTO =
                new CompanyUpdateDTO("New Company Name", "New Tax Number");

        Company oldCompany = new Company("Old Company Name", "Old Tax Number", user);
        oldCompany.setCompanyId(companyId);

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(oldCompany));

        companyService.updateCompany(companyId, companyUpdateDTO);

        verify(companyRepository, times(1)).findById(companyId);
        verify(companyRepository, times(1)).save(oldCompany);
    }

    @Test
    @DisplayName("Should throw a ResponseStatusException when the company id is not found")
    void deleteCompanyByIdWhenCompanyIdNotFoundThenThrowException() {
        UUID companyId = UUID.randomUUID();

        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

        assertThrows(
                ResponseStatusException.class, () -> companyService.deleteCompanyById(companyId));

        verify(companyRepository, times(1)).findById(companyId);
        verify(companyRepository, never()).deleteById(companyId);
    }

    @Test
    @DisplayName("Should delete the company when the company id is valid")
    void deleteCompanyByIdWhenCompanyIdIsValid() {
        UUID companyId = UUID.randomUUID();
        Company company =
                new Company("Test Company", "123456789", user);

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));

        companyService.deleteCompanyById(companyId);

        verify(companyRepository, times(1)).deleteById(companyId);
    }
}