package me.project.entitiy;

import me.project.auth.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
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

    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    public Company(String companyName, String taxNumber, User user){
        setCompanyName(companyName);
        setTaxNumber(taxNumber);
        setUser(user);
    }

}
