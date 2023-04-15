package me.project.auth;

import me.project.auth.enums.AppUserRole;
import me.project.auth.enums.AuthProvider;
import me.project.entitiy.Bike;
import me.project.entitiy.File;
import me.project.entitiy.Order;
import me.project.entitiy.UserCompany;
import me.project.dtos.request.UserCreateDTO;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(
            strategy = GenerationType.AUTO
    )
    @Column(
            nullable = false,
            updatable = false
    )
    private UUID userId;

    @ManyToOne
    @JoinColumn(name = "file_id")
    @JsonBackReference
    private File avatar;

    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumberPrefix;
    private String phoneNumber;
    private String tags;
    private String note;
    private String password;
    private Boolean isPasswordChangeRequired;

    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Bike> bikes;

    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Order> orders;

    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<UserCompany> userCompanies;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AppUserRole appUserRole;

    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    private String providerId;

    private Boolean locked;
    private Boolean enabled;

    public User(UserCreateDTO userCredentials,
                Boolean locked,
                Boolean enabled) {
        this.password = userCredentials.getPassword();
        this.email = userCredentials.getEmail();
        this.appUserRole = userCredentials.getAppUserRole();
        this.note = userCredentials.getNote();
        this.firstName = userCredentials.getFirstName();
        this.lastName = userCredentials.getLastName();
        this.phoneNumberPrefix = userCredentials.getPhoneNumberPrefix();
        this.phoneNumber = userCredentials.getPhoneNumber();
        this.isPasswordChangeRequired = true;
        this.password = userCredentials.getPassword();
        this.locked = locked;
        this.enabled = enabled;
    }

    public User() {
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(("ROLE_" + appUserRole.name()));
        return Collections.singletonList(authority);
    }

    @Override
    public String getPassword() {return password;}

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
