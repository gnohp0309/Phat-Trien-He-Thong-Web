package vn.edu.demo_spmvc.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    private String name;

    private BigDecimal price;

    private Integer quantity;

    private boolean deleted = false;

    @OneToMany(mappedBy = "product")
    private List<OrderDetail> orderDetails;
}
