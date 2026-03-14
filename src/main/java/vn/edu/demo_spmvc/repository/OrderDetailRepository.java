package vn.edu.demo_spmvc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.demo_spmvc.entity.OrderDetail;

public interface OrderDetailRepository extends JpaRepository<OrderDetail , Long> {
    boolean existsByProductId(Long productId);
}
