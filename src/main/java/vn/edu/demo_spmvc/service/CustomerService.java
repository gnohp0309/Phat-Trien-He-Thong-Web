package vn.edu.demo_spmvc.service;

import vn.edu.demo_spmvc.DTO.CustomerCreateRequest;
import vn.edu.demo_spmvc.DTO.CustomerResponse;
import vn.edu.demo_spmvc.DTO.OrderResponse;

import java.util.List;

public interface CustomerService {
    CustomerResponse create(CustomerCreateRequest req);

    List<CustomerResponse> getAll();

    List<OrderResponse> getOrders(Long customerId);
}

