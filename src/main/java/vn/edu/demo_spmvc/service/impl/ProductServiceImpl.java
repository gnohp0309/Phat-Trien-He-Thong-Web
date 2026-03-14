package vn.edu.demo_spmvc.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vn.edu.demo_spmvc.DTO.PageResponse;
import vn.edu.demo_spmvc.DTO.ProductCreateRequest;
import vn.edu.demo_spmvc.DTO.ProductResponse;
import vn.edu.demo_spmvc.DTO.ProductUpdateRequest;
import vn.edu.demo_spmvc.entity.Product;
import vn.edu.demo_spmvc.exception.AppException;
import vn.edu.demo_spmvc.mapper.ProductMapper;
import vn.edu.demo_spmvc.repository.OrderDetailRepository;
import vn.edu.demo_spmvc.repository.ProductRepository;
import vn.edu.demo_spmvc.service.ProductService;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository repo;
    private final OrderDetailRepository orderDetailRepo;

    @Override
    public ProductResponse create(ProductCreateRequest dto) {
        Product product = ProductMapper.toEntity(dto);
        return ProductMapper.toResponse(repo.save(product));
    }

    @Override
    public ProductResponse update(Long id, ProductUpdateRequest dto) {
        Product p = repo.findById(id)
                .filter(pr -> !pr.isDeleted())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "PRODUCT_NOT_FOUND", "Product not found"));
        ProductMapper.updateEntity(p, dto);
        return ProductMapper.toResponse(repo.save(p));
    }

    @Override
    public void delete(Long id) {
        Product p = repo.findById(id)
                .filter(pr -> !pr.isDeleted())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "PRODUCT_NOT_FOUND", "Product not found"));

        if (orderDetailRepo.existsByProductId(id)) {
            throw new AppException(HttpStatus.CONFLICT, "PRODUCT_IN_ORDER", "Cannot delete product because it was used in orders");
        }

        p.setDeleted(true);
        repo.save(p);
    }

    @Override
    public PageResponse<ProductResponse> getAll(int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size, parseSort(sort));
        Page<Product> result = repo.findByDeletedFalse(pageable);
        List<ProductResponse> items = result.getContent().stream().map(ProductMapper::toResponse).toList();
        return new PageResponse<>(items, result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages());
    }

    @Override
    public PageResponse<ProductResponse> search(String name, BigDecimal minPrice, BigDecimal maxPrice, Boolean inStock, int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size, parseSort(sort));
        Page<Product> result = repo.search(name, minPrice, maxPrice, inStock, pageable);
        List<ProductResponse> items = result.getContent().stream().map(ProductMapper::toResponse).toList();
        return new PageResponse<>(items, result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages());
    }

    private Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Order.asc("id"));
        }
        // expected: field,dir  ex: price,desc
        String[] parts = sort.split(",");
        String field = parts[0].trim();
        String dir = parts.length > 1 ? parts[1].trim() : "asc";
        return "desc".equalsIgnoreCase(dir) ? Sort.by(Sort.Order.desc(field)) : Sort.by(Sort.Order.asc(field));
    }
}
