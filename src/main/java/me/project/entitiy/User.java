package me.project.entitiy;

import me.project.auth.enums.AppUserRole;
import me.project.auth.enums.AuthProvider;
import me.project.dtos.request.user.ClientCreateDTO;
import me.project.dtos.request.user.CustomerRegisterDTO;
import me.project.dtos.request.user.UserCreateDTO;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private String note;
    private String password;
    private Boolean isPasswordChangeRequired;

    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Bike> bikes;

    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Order> orders;

    @OneToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @OneToOne
    @JoinColumn(name = "address_id")
    private Address address;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AppUserRole appUserRole;

    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    private String providerId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime createdOn;

    private Boolean locked;
    private Boolean enabled;

    public User(UserCreateDTO userCredentials,
                Boolean locked,
                Boolean enabled) {
        this.password = userCredentials.getPassword().trim();
        this.email = userCredentials.getEmail().trim();
        this.appUserRole = userCredentials.getAppUserRole();
        this.note = userCredentials.getNote().trim();
        this.firstName = userCredentials.getFirstName().trim();
        this.lastName = userCredentials.getLastName().trim();
        this.phoneNumberPrefix = userCredentials.getPhoneNumberPrefix().trim();
        this.phoneNumber = userCredentials.getPhoneNumber().trim();
        this.isPasswordChangeRequired = false;
        this.locked = locked;
        this.enabled = enabled;
        this.createdOn = LocalDateTime.now();
    }

    public User(ClientCreateDTO clientCreateDTO,
                Boolean locked,
                Boolean enabled) {
        this.email = clientCreateDTO.getEmail().trim();
        this.appUserRole = AppUserRole.CLIENT;
        if(clientCreateDTO.getNote() != null)
            this.note = clientCreateDTO.getNote().trim();
        this.firstName = clientCreateDTO.getFirstName().trim();
        this.lastName = clientCreateDTO.getLastName().trim();
        this.phoneNumberPrefix = clientCreateDTO.getPhoneNumberPrefix().trim();
        this.phoneNumber = clientCreateDTO.getPhoneNumber().trim();
        this.isPasswordChangeRequired = true;
        this.locked = locked;
        this.enabled = enabled;
        this.createdOn = LocalDateTime.now();
    }

    public User() {
    }

    public User(CustomerRegisterDTO customerRegisterDTO,
                boolean locked,
                boolean enabled,
                AppUserRole role) {
        this.email = customerRegisterDTO.getEmail().trim();
        this.appUserRole = role;
        this.firstName = customerRegisterDTO.getFirstName().trim();
        this.lastName = customerRegisterDTO.getLastName().trim();
        this.note = customerRegisterDTO.getNote().trim();
        this.phoneNumberPrefix = customerRegisterDTO.getPhoneNumberPrefix().trim();
        this.phoneNumber = customerRegisterDTO.getPhoneNumber().trim();
        this.isPasswordChangeRequired = true;
        this.locked = locked;
        this.enabled = enabled;
        this.createdOn = LocalDateTime.now();
    }

    public User(String email, String firstName, String lastName, AppUserRole appUserRole, Boolean locked, Boolean enabled) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.appUserRole = appUserRole;
        this.locked = locked;
        this.enabled = enabled;
        this.createdOn = LocalDateTime.now();
        this.isPasswordChangeRequired = true;
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
