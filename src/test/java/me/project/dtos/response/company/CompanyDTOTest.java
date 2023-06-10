package me.project.dtos.response.company;

import me.project.entitiy.Company;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("CompanyDTO")
class CompanyDTOTest {

    @Test
    @DisplayName("Should convert a Company entity to a CompanyDTO object")
    void convertFromEntityToCompanyDTO() {// Create a mock Company object
        Company company = mock(Company.class);
        UUID companyId = UUID.randomUUID();
        String companyName = "Test Company";
        String taxNumber = "1234567890";
        // Set up the mock object
        when(company.getCompanyId()).thenReturn(companyId);
        when(company.getCompanyName()).thenReturn(companyName);
        when(company.getTaxNumber()).thenReturn(taxNumber);

        // Call the method being tested
        CompanyDTO companyDTO = CompanyDTO.convertFromEntity(company);

        // Verify the results
        assertEquals(companyId, companyDTO.getCompanyId());
        assertEquals(companyName, companyDTO.getCompanyName());
        assertEquals(taxNumber, companyDTO.getTaxNumber());
    }

}