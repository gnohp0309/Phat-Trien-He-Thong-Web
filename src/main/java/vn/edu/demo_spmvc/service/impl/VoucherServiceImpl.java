package vn.edu.demo_spmvc.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vn.edu.demo_spmvc.DTO.VoucherCreateRequest;
import vn.edu.demo_spmvc.DTO.VoucherResponse;
import vn.edu.demo_spmvc.entity.Voucher;
import vn.edu.demo_spmvc.exception.AppException;
import vn.edu.demo_spmvc.repository.VoucherRepository;
import vn.edu.demo_spmvc.service.VoucherService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VoucherServiceImpl implements VoucherService {
    private final VoucherRepository repo;

    @Override
    public VoucherResponse create(VoucherCreateRequest req) {
        if (repo.findByCode(req.getCode().trim()).isPresent()) {
            throw new AppException(HttpStatus.CONFLICT, "VOUCHER_EXISTS", "Voucher code already exists");
        }
        Voucher v = new Voucher();
        v.setCode(req.getCode().trim());
        v.setType(req.getType());
        v.setValue(req.getValue());
        v.setExpiresAt(req.getExpiresAt());
        v.setActive(true);
        v = repo.save(v);
        return toResponse(v);
    }

    @Override
    public List<VoucherResponse> getAll() {
        return repo.findAll().stream().map(this::toResponse).toList();
    }

    private VoucherResponse toResponse(Voucher v) {
        return new VoucherResponse(v.getId(), v.getCode(), v.getType(), v.getValue(), v.getExpiresAt(), v.isActive());
    }
}

