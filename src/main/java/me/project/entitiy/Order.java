package me.project.entitiy;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(
            strategy = GenerationType.AUTO
    )
    @Column(
            nullable = false,
            updatable = false
    )
    private UUID orderId;

    private String note;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime createdOn;

    @ManyToOne
    @JoinColumn(name = "bike_id")
    @JsonBackReference
    private Bike bike;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    @ManyToOne
    @JoinColumn(name = "order_status_id")
    @JsonBackReference
    private OrderStatus orderStatus;

    @OneToMany(mappedBy = "order",fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<OrderPart> orderParts;

    @OneToMany(mappedBy = "order",fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<OrderService> orderServices;

    public Order(String note,
                 LocalDateTime createdOn,
                 Bike bike,
                 User user,
                 OrderStatus orderStatus) {
        this.note = note;
        this.createdOn = createdOn;
        this.bike = bike;
        this.user = user;
        this.orderStatus = orderStatus;
    }
}
