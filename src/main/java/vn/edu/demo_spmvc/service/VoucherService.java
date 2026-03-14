package vn.edu.demo_spmvc.service;

import vn.edu.demo_spmvc.DTO.VoucherCreateRequest;
import vn.edu.demo_spmvc.DTO.VoucherResponse;

import java.util.List;

public interface VoucherService {
    VoucherResponse create(VoucherCreateRequest req);

    List<VoucherResponse> getAll();
}

