package vn.edu.demo_spmvc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.demo_spmvc.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}

