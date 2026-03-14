package vn.edu.demo_spmvc.mapper;

import vn.edu.demo_spmvc.DTO.ProductCreateRequest;
import vn.edu.demo_spmvc.DTO.ProductResponse;
import vn.edu.demo_spmvc.DTO.ProductUpdateRequest;
import vn.edu.demo_spmvc.entity.Product;

public class ProductMapper {
    public static Product toEntity(ProductCreateRequest dto){
        Product p = new Product();
        p.setName(dto.getName());
        p.setPrice(dto.getPrice());
        p.setQuantity(dto.getQuantity());
        return p;
    }

    public static void updateEntity(Product p, ProductUpdateRequest dto) {
        p.setName(dto.getName());
        p.setPrice(dto.getPrice());
        p.setQuantity(dto.getQuantity());
    }

    public static ProductResponse toResponse(Product p) {
        return new ProductResponse(p.getId(), p.getName(), p.getPrice(), p.getQuantity());
    }
}
