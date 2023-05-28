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
public class BikeFile {
    @Id
    @GeneratedValue(
            strategy = GenerationType.AUTO
    )
    @Column(
            nullable = false,
            updatable = false
    )
    private UUID bikeFileId;

    @ManyToOne
    @JoinColumn(name = "bike_id")
    @JsonBackReference
    private Bike bike;

    @ManyToOne
    @JoinColumn(name = "file_id")
    @JsonBackReference
    private File file;

    private Integer orderNumber;

    public BikeFile(Bike bike, File file, Integer orderNumber) {
        this.bike = bike;
        this.file = file;
        this.orderNumber= orderNumber;
    }
}
