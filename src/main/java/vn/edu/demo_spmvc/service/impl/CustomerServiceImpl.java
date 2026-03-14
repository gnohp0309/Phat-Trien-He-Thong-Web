package vn.edu.demo_spmvc.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vn.edu.demo_spmvc.DTO.CustomerCreateRequest;
import vn.edu.demo_spmvc.DTO.CustomerResponse;
import vn.edu.demo_spmvc.DTO.OrderResponse;
import vn.edu.demo_spmvc.entity.Customer;
import vn.edu.demo_spmvc.exception.AppException;
import vn.edu.demo_spmvc.mapper.OrderMapper;
import vn.edu.demo_spmvc.repository.CustomerRepository;
import vn.edu.demo_spmvc.repository.OrderRepository;
import vn.edu.demo_spmvc.service.CustomerService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepo;
    private final OrderRepository orderRepo;

    @Override
    public CustomerResponse create(CustomerCreateRequest req) {
        Customer c = new Customer();
        c.setName(req.getName().trim());
        c = customerRepo.save(c);
        return new CustomerResponse(c.getId(), c.getName());
    }

    @Override
    public List<CustomerResponse> getAll() {
        return customerRepo.findAll().stream()
                .map(c -> new CustomerResponse(c.getId(), c.getName()))
                .toList();
    }

    @Override
    public List<OrderResponse> getOrders(Long customerId) {
        if (!customerRepo.existsById(customerId)) {
            throw new AppException(HttpStatus.NOT_FOUND, "CUSTOMER_NOT_FOUND", "Customer not found");
        }
        return orderRepo.findByCustomerId(customerId).stream()
                .map(OrderMapper::toResponse)
                .toList();
    }
}

