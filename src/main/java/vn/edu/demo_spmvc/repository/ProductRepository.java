package vn.edu.demo_spmvc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.edu.demo_spmvc.entity.Product;

import java.math.BigDecimal;

public interface ProductRepository extends JpaRepository<Product,Long> {
    Page<Product> findByDeletedFalse(Pageable pageable);

    @Query("""
            select p
            from Product p
            where p.deleted = false
              and (:name is null or lower(p.name) like lower(concat('%', :name, '%')))
              and (:minPrice is null or p.price >= :minPrice)
              and (:maxPrice is null or p.price <= :maxPrice)
              and (:inStock is null or (:inStock = true and p.quantity > 0) or (:inStock = false and p.quantity <= 0))
            """)
    Page<Product> search(
            @Param("name") String name,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("inStock") Boolean inStock,
            Pageable pageable
    );
}
