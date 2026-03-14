package vn.edu.demo_spmvc.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.edu.demo_spmvc.DTO.RevenueByDayResponse;
import vn.edu.demo_spmvc.DTO.RevenueByMonthResponse;
import vn.edu.demo_spmvc.DTO.TopProductResponse;
import vn.edu.demo_spmvc.service.ReportService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService service;

    @GetMapping("/revenue/daily")
    @PreAuthorize("hasRole('ADMIN')")
    public List<RevenueByDayResponse> revenueDaily(
            @RequestParam(name = "from") LocalDate from,
            @RequestParam(name = "toExclusive") LocalDate toExclusive
    ) {
        return service.revenueByDay(from, toExclusive);
    }

    @GetMapping("/revenue/monthly")
    @PreAuthorize("hasRole('ADMIN')")
    public List<RevenueByMonthResponse> revenueMonthly(@RequestParam(name = "year") int year) {
        return service.revenueByMonth(year);
    }

    @GetMapping("/top-products")
    @PreAuthorize("hasRole('ADMIN')")
    public List<TopProductResponse> topProducts(@RequestParam(name = "limit", defaultValue = "5") int limit) {
        return service.topProducts(limit);
    }
}

