package me.project.entitiy;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "Company")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Company {
    @Id
    @GeneratedValue(
            strategy = GenerationType.AUTO
    )
    @Column(
            nullable = false,
            updatable = false
    )
    private UUID companyId;
    private String companyName;
    private String taxNumber;

    @OneToMany(mappedBy = "company",fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Address> addresses;

    @OneToMany(mappedBy = "company",fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<UserCompany> userCompanies;
}
