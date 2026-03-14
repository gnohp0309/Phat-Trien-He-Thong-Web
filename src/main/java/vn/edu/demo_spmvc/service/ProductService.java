package vn.edu.demo_spmvc.service;

import vn.edu.demo_spmvc.DTO.PageResponse;
import vn.edu.demo_spmvc.DTO.ProductCreateRequest;
import vn.edu.demo_spmvc.DTO.ProductResponse;
import vn.edu.demo_spmvc.DTO.ProductUpdateRequest;

import java.util.List;
import java.math.BigDecimal;

public interface ProductService {
    ProductResponse create(ProductCreateRequest dto);

    ProductResponse update(Long id, ProductUpdateRequest dto);

    void delete(Long id);

    PageResponse<ProductResponse> getAll(int page, int size, String sort);

    PageResponse<ProductResponse> search(String name, BigDecimal minPrice, BigDecimal maxPrice, Boolean inStock, int page, int size, String sort);

}
