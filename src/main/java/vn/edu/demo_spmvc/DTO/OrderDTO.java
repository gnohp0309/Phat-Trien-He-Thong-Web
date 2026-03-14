package vn.edu.demo_spmvc.DTO;

import lombok.Data;

@Data
public class OrderDTO {
    private Long productId;
    private Integer quantity;
    private String customerName;
}
