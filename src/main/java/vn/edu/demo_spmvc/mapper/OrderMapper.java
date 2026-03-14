package vn.edu.demo_spmvc.mapper;

import vn.edu.demo_spmvc.DTO.OrderDetailResponse;
import vn.edu.demo_spmvc.DTO.OrderResponse;
import vn.edu.demo_spmvc.entity.Order;
import vn.edu.demo_spmvc.entity.OrderDetail;

import java.util.List;

public class OrderMapper {
    public static OrderResponse toResponse(Order order) {
        OrderResponse res = new OrderResponse();
        res.setId(order.getId());
        res.setOrderDate(order.getOrderDate());
        res.setTotalAmount(order.getTotalAmount());
        res.setDiscountAmount(order.getDiscountAmount());
        res.setVoucherCode(order.getVoucherCode());
        res.setStatus(order.getStatus());

        if (order.getCustomer() != null) {
            res.setCustomerId(order.getCustomer().getId());
            res.setCustomerName(order.getCustomer().getName());
        } else {
            res.setCustomerName(order.getCustomerName());
        }

        List<OrderDetailResponse> details = order.getOrderDetails() == null ? List.of() :
                order.getOrderDetails().stream().map(OrderMapper::toDetailResponse).toList();
        res.setDetails(details);
        return res;
    }

    private static OrderDetailResponse toDetailResponse(OrderDetail d) {
        OrderDetailResponse r = new OrderDetailResponse();
        if (d.getProduct() != null) {
            r.setProductId(d.getProduct().getId());
            r.setProductName(d.getProduct().getName());
        }
        r.setQuantity(d.getQuantity());
        r.setPrice(d.getPrice());
        r.setSubTotal(d.getSubTotal());
        return r;
    }
}

