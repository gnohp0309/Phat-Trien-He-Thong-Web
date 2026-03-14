package vn.edu.demo_spmvc.DTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDTO {
    private String name;

    private BigDecimal price;

    private Integer quantity;
}
