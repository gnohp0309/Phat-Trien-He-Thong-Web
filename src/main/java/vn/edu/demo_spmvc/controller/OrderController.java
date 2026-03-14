package vn.edu.demo_spmvc.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.demo_spmvc.DTO.OrderCreateRequest;
import vn.edu.demo_spmvc.DTO.OrderResponse;
import vn.edu.demo_spmvc.service.OrderService;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService service;

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public OrderResponse createOrder(@Valid @RequestBody OrderCreateRequest dto){
        return service.createOrder(dto);
    }

    @PostMapping("/{id}/pay")
    @PreAuthorize("hasRole('ADMIN')")
    public OrderResponse pay(@PathVariable Long id) {
        return service.pay(id);
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("hasRole('ADMIN')")
    public OrderResponse complete(@PathVariable Long id) {
        return service.complete(id);
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public OrderResponse cancel(@PathVariable Long id) {
        return service.cancel(id);
    }
}

