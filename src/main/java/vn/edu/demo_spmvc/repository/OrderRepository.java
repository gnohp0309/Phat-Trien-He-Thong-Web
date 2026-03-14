package vn.edu.demo_spmvc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.demo_spmvc.entity.Order;
import vn.edu.demo_spmvc.entity.OrderStatus;
import vn.edu.demo_spmvc.DTO.RevenueByDayResponse;
import vn.edu.demo_spmvc.DTO.RevenueByMonthResponse;
import vn.edu.demo_spmvc.DTO.TopProductResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerId(Long customerId);

    @Query("""
            select new vn.edu.demo_spmvc.DTO.RevenueByDayResponse(
                function('date', o.orderDate),
                coalesce(sum(o.totalAmount), 0)
            )
            from Order o
            where o.status = :status
              and o.orderDate >= :from
              and o.orderDate < :to
            group by function('date', o.orderDate)
            order by function('date', o.orderDate)
            """)
    List<RevenueByDayResponse> revenueByDay(
            @Param("status") OrderStatus status,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    @Query("""
            select new vn.edu.demo_spmvc.DTO.RevenueByMonthResponse(
                function('year', o.orderDate),
                function('month', o.orderDate),
                coalesce(sum(o.totalAmount), 0)
            )
            from Order o
            where o.status = :status
              and function('year', o.orderDate) = :year
            group by function('year', o.orderDate), function('month', o.orderDate)
            order by function('month', o.orderDate)
            """)
    List<RevenueByMonthResponse> revenueByMonth(
            @Param("status") OrderStatus status,
            @Param("year") int year
    );

    @Query("""
            select new vn.edu.demo_spmvc.DTO.TopProductResponse(
                p.id,
                p.name,
                sum(od.quantity)
            )
            from OrderDetail od
            join od.order o
            join od.product p
            where o.status = :status
            group by p.id, p.name
            order by sum(od.quantity) desc
            """)
    List<TopProductResponse> topProducts(@Param("status") OrderStatus status);
}
