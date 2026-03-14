package vn.edu.demo_spmvc.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.edu.demo_spmvc.DTO.CustomerCreateRequest;
import vn.edu.demo_spmvc.DTO.CustomerResponse;
import vn.edu.demo_spmvc.DTO.OrderResponse;
import vn.edu.demo_spmvc.service.CustomerService;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService service;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public CustomerResponse create(@Valid @RequestBody CustomerCreateRequest req) {
        return service.create(req);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<CustomerResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}/orders")
    @PreAuthorize("hasRole('ADMIN')")
    public List<OrderResponse> getOrders(@PathVariable Long id) {
        return service.getOrders(id);
    }
}

