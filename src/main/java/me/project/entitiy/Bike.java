package me.project.entitiy;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "Bike")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Bike {
    @Id
    @GeneratedValue(
            strategy = GenerationType.AUTO
    )
    @Column(
            nullable = false,
            updatable = false
    )
    private UUID bikeId;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    private String bikeName;
    private String bikeMake;
    private String bikeModel;
    private String serialNumber;

    @Nullable
    private Integer yearOfProduction;

    @OneToMany(mappedBy = "bike",fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<BikeFile> bikeFiles;

    @OneToMany(mappedBy = "bike",fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Order> orders;

    public Bike(User user,
                String bikeName,
                String bikeMake,
                String bikeModel,
                String serialNumber,
                @Nullable Integer yearOfProduction) {
        this.user = user;
        this.bikeName = bikeName;
        this.bikeMake = bikeMake;
        this.bikeModel = bikeModel;
        this.serialNumber = serialNumber;
        this.yearOfProduction = yearOfProduction;
    }
}
