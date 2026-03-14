package vn.edu.demo_spmvc.service;

import vn.edu.demo_spmvc.DTO.RevenueByDayResponse;
import vn.edu.demo_spmvc.DTO.RevenueByMonthResponse;
import vn.edu.demo_spmvc.DTO.TopProductResponse;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {
    List<RevenueByDayResponse> revenueByDay(LocalDate from, LocalDate toExclusive);

    List<RevenueByMonthResponse> revenueByMonth(int year);

    List<TopProductResponse> topProducts(int limit);
}

