package me.project.entitiy;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderPart {
    @Id
    @GeneratedValue(
            strategy = GenerationType.AUTO
    )
    @Column(
            nullable = false
    )
    private UUID orderPartId;


    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonBackReference
    private Order order;

    private String orderCode;
    private String orderName;
    private BigDecimal orderPrice;

    public OrderPart(String orderCode, String orderName, BigDecimal orderPrice) {
        this.orderCode = orderCode;
        this.orderName = orderName;
        this.orderPrice = orderPrice;
    }
}
