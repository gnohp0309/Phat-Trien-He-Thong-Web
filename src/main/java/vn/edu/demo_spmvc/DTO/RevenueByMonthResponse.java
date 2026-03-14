package vn.edu.demo_spmvc.DTO;

import java.math.BigDecimal;

public class RevenueByMonthResponse {
    private Long year;
    private Long month;
    private BigDecimal revenue;

    public RevenueByMonthResponse(Long year, Long month, BigDecimal revenue) {
        this.year = year;
        this.month = month;
        this.revenue = revenue;
    }

    public Long getYear() {
        return year;
    }

    public Long getMonth() {
        return month;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    // Fallback constructor for Hibernate when using generic Object/Number types
    public RevenueByMonthResponse(Object year, Object month, Object revenue) {
        if (year instanceof Number n) {
            this.year = n.longValue();
        } else {
            this.year = null;
        }
        if (month instanceof Number n) {
            this.month = n.longValue();
        } else {
            this.month = null;
        }
        if (revenue instanceof BigDecimal bd) {
            this.revenue = bd;
        } else if (revenue instanceof Number n) {
            this.revenue = BigDecimal.valueOf(n.doubleValue());
        } else {
            this.revenue = BigDecimal.ZERO;
        }
    }
}

