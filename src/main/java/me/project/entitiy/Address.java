package me.project.entitiy;

import me.project.auth.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    @Id
    @GeneratedValue(
            strategy = GenerationType.AUTO
    )
    @Column(
            nullable = false,
            updatable = false
    )
    private UUID addressId;

    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @ManyToOne
    @JoinColumn(name = "country_id")
    @JsonBackReference
    private Country country;

    private String streetName;
    private String postCode;
    private String city;

    public Address(User user, Country country, String streetName, String postCode, String city) {
        this.user = user;
        this.country = country;
        this.streetName = streetName;
        this.postCode = postCode;
        this.city = city;
    }
}
