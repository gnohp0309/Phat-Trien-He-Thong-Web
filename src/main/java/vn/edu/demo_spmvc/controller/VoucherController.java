package vn.edu.demo_spmvc.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.edu.demo_spmvc.DTO.VoucherCreateRequest;
import vn.edu.demo_spmvc.DTO.VoucherResponse;
import vn.edu.demo_spmvc.service.VoucherService;

import java.util.List;

@RestController
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
public class VoucherController {
    private final VoucherService service;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public VoucherResponse create(@Valid @RequestBody VoucherCreateRequest req) {
        return service.create(req);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<VoucherResponse> getAll() {
        return service.getAll();
    }
}

