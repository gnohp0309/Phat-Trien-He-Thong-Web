package vn.edu.demo_spmvc.DTO;

import vn.edu.demo_spmvc.entity.VoucherType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class VoucherResponse {
    private Long id;
    private String code;
    private VoucherType type;
    private BigDecimal value;
    private LocalDateTime expiresAt;
    private boolean active;

    public VoucherResponse(Long id, String code, VoucherType type, BigDecimal value, LocalDateTime expiresAt, boolean active) {
        this.id = id;
        this.code = code;
        this.type = type;
        this.value = value;
        this.expiresAt = expiresAt;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public VoucherType getType() {
        return type;
    }

    public BigDecimal getValue() {
        return value;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public boolean isActive() {
        return active;
    }
}

