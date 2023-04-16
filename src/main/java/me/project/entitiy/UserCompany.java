package me.project.entitiy;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCompany {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(
            nullable = false,
            updatable = false
    )
    private UUID userCompanyId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    @ManyToOne
    @JoinColumn(name = "company_id")
    @JsonBackReference
    private Company company;
}
