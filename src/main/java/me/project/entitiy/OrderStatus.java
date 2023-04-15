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
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(
            nullable = false,
            updatable = false
    )
    private UUID orderStatusId;
    private String orderStatusName;
    private Integer orderSort;

    @OneToMany(mappedBy = "orderStatus",fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Order> orders;
}
