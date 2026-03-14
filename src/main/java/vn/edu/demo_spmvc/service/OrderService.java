package vn.edu.demo_spmvc.service;

import vn.edu.demo_spmvc.DTO.OrderCreateRequest;
import vn.edu.demo_spmvc.DTO.OrderResponse;

public interface OrderService {
    OrderResponse createOrder(OrderCreateRequest dto);

    OrderResponse pay(Long orderId);

    OrderResponse complete(Long orderId);

    OrderResponse cancel(Long orderId);

}
