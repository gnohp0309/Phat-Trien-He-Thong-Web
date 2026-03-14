package vn.edu.demo_spmvc.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.demo_spmvc.DTO.RevenueByDayResponse;
import vn.edu.demo_spmvc.DTO.RevenueByMonthResponse;
import vn.edu.demo_spmvc.DTO.TopProductResponse;
import vn.edu.demo_spmvc.entity.OrderStatus;
import vn.edu.demo_spmvc.repository.OrderRepository;
import vn.edu.demo_spmvc.service.ReportService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final OrderRepository orderRepo;

    @Override
    public List<RevenueByDayResponse> revenueByDay(LocalDate from, LocalDate toExclusive) {
        LocalDateTime fromDt = from.atStartOfDay();
        LocalDateTime toDt = toExclusive.atStartOfDay();
        return orderRepo.revenueByDay(OrderStatus.COMPLETED, fromDt, toDt);
    }

    @Override
    public List<RevenueByMonthResponse> revenueByMonth(int year) {
        return orderRepo.revenueByMonth(OrderStatus.COMPLETED, year);
    }

    @Override
    public List<TopProductResponse> topProducts(int limit) {
        List<TopProductResponse> all = orderRepo.topProducts(OrderStatus.COMPLETED);
        if (limit <= 0 || all.isEmpty()) return List.of();
        return all.subList(0, Math.min(limit, all.size()));
    }
}

