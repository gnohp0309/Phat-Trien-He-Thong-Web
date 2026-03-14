package vn.edu.demo_spmvc.DTO;

import java.math.BigDecimal;
import java.sql.Date;

public class RevenueByDayResponse {
    private Date date;
    private BigDecimal revenue;

    public RevenueByDayResponse(Date date, BigDecimal revenue) {
        this.date = date;
        this.revenue = revenue;
    }

    public Date getDate() {
        return date;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    // Fallback constructor for Hibernate when it uses generic Object types
    public RevenueByDayResponse(Object date, Object revenue) {
        if (date instanceof Date d) {
            this.date = d;
        } else {
            this.date = null;
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

