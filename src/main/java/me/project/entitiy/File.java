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
@Table(name = "File")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class File {
    @Id
    @GeneratedValue (
            strategy = GenerationType.AUTO
    )
    @Column(
            nullable = false,
            updatable = false
    )
    private UUID fileId;
    private String fileName;

    @OneToMany(mappedBy = "file",fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<BikeFile> bikeFiles;

    @OneToMany(mappedBy = "avatar",fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<User> users;

    public File(UUID fileId, String fileName) {
        this.fileId = fileId;
        this.fileName = fileName;
    }
}
