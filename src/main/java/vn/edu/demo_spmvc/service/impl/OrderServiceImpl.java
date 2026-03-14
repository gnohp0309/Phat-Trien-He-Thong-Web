package vn.edu.demo_spmvc.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vn.edu.demo_spmvc.DTO.OrderCreateRequest;
import vn.edu.demo_spmvc.DTO.OrderItemRequest;
import vn.edu.demo_spmvc.DTO.OrderResponse;
import vn.edu.demo_spmvc.entity.Customer;
import vn.edu.demo_spmvc.entity.Order;
import vn.edu.demo_spmvc.entity.OrderDetail;
import vn.edu.demo_spmvc.entity.OrderStatus;
import vn.edu.demo_spmvc.entity.Product;
import vn.edu.demo_spmvc.entity.Voucher;
import vn.edu.demo_spmvc.entity.VoucherType;
import vn.edu.demo_spmvc.exception.AppException;
import vn.edu.demo_spmvc.mapper.OrderMapper;
import vn.edu.demo_spmvc.repository.CustomerRepository;
import vn.edu.demo_spmvc.repository.OrderRepository;
import vn.edu.demo_spmvc.repository.ProductRepository;
import vn.edu.demo_spmvc.repository.VoucherRepository;
import vn.edu.demo_spmvc.service.OrderService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final ProductRepository productRepo;
    private final OrderRepository orderRepo;
    private final CustomerRepository customerRepo;
    private final VoucherRepository voucherRepo;

    @Override
    @Transactional
    public OrderResponse createOrder(OrderCreateRequest dto) {
        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        if (dto.getCustomerId() != null) {
            Customer c = customerRepo.findById(dto.getCustomerId())
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "CUSTOMER_NOT_FOUND", "Customer not found"));
            order.setCustomer(c);
            order.setCustomerName(c.getName());
        } else if (dto.getCustomerName() != null && !dto.getCustomerName().isBlank()) {
            order.setCustomerName(dto.getCustomerName().trim());
        } else {
            throw new AppException(HttpStatus.BAD_REQUEST, "CUSTOMER_REQUIRED", "customerId or customerName is required");
        }

        Map<Long, Integer> merged = mergeItems(dto.getItems());

        List<OrderDetail> details = merged.entrySet().stream().map(e -> {
            Long productId = e.getKey();
            Integer qty = e.getValue();

            Product product = productRepo.findById(productId)
                    .filter(p -> !p.isDeleted())
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "PRODUCT_NOT_FOUND", "Product not found: " + productId));

            if (product.getQuantity() == null || product.getQuantity() < qty) {
                throw new AppException(HttpStatus.BAD_REQUEST, "NOT_ENOUGH_STOCK", "Not enough stock for product: " + productId);
            }

            BigDecimal subTotal = product.getPrice().multiply(BigDecimal.valueOf(qty));

            OrderDetail d = new OrderDetail();
            d.setOrder(order);
            d.setProduct(product);
            d.setQuantity(qty);
            d.setPrice(product.getPrice());
            d.setSubTotal(subTotal);

            product.setQuantity(product.getQuantity() - qty);
            productRepo.save(product);

            return d;
        }).toList();

        order.setOrderDetails(details);

        BigDecimal total = details.stream()
                .map(OrderDetail::getSubTotal)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discount = BigDecimal.ZERO;
        if (dto.getVoucherCode() != null && !dto.getVoucherCode().isBlank()) {
            Voucher v = voucherRepo.findByCode(dto.getVoucherCode().trim())
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "VOUCHER_NOT_FOUND", "Voucher not found"));
            validateVoucher(v);
            discount = calculateDiscount(total, v);
            order.setVoucherCode(v.getCode());
        }

        order.setDiscountAmount(discount);
        order.setTotalAmount(clampNonNegative(total.subtract(discount)));

        Order saved = orderRepo.save(order);
        return OrderMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public OrderResponse pay(Long orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "ORDER_NOT_FOUND", "Order not found"));
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new AppException(HttpStatus.BAD_REQUEST, "INVALID_STATUS", "Only PENDING orders can be paid");
        }
        order.setStatus(OrderStatus.PAID);
        return OrderMapper.toResponse(orderRepo.save(order));
    }

    @Override
    @Transactional
    public OrderResponse complete(Long orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "ORDER_NOT_FOUND", "Order not found"));
        if (order.getStatus() != OrderStatus.PAID) {
            throw new AppException(HttpStatus.BAD_REQUEST, "INVALID_STATUS", "Only PAID orders can be completed");
        }
        order.setStatus(OrderStatus.COMPLETED);
        return OrderMapper.toResponse(orderRepo.save(order));
    }

    @Override
    @Transactional
    public OrderResponse cancel(Long orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "ORDER_NOT_FOUND", "Order not found"));

        if (order.getStatus() == OrderStatus.PAID) {
            throw new AppException(HttpStatus.BAD_REQUEST, "CANNOT_CANCEL", "Cannot cancel a paid order");
        }
        if (order.getStatus() == OrderStatus.COMPLETED) {
            throw new AppException(HttpStatus.BAD_REQUEST, "CANNOT_CANCEL", "Cannot cancel a completed order");
        }
        if (order.getStatus() == OrderStatus.CANCELLED) {
            return OrderMapper.toResponse(order);
        }

        // restore stock (optimistic lock via Product.version)
        if (order.getOrderDetails() != null) {
            for (OrderDetail d : order.getOrderDetails()) {
                if (d.getProduct() == null) continue;
                Product p = productRepo.findById(d.getProduct().getId())
                        .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "PRODUCT_NOT_FOUND", "Product not found"));
                p.setQuantity((p.getQuantity() == null ? 0 : p.getQuantity()) + (d.getQuantity() == null ? 0 : d.getQuantity()));
                productRepo.save(p);
            }
        }

        order.setStatus(OrderStatus.CANCELLED);
        return OrderMapper.toResponse(orderRepo.save(order));
    }

    private Map<Long, Integer> mergeItems(List<OrderItemRequest> items) {
        Map<Long, Integer> merged = new LinkedHashMap<>();
        for (OrderItemRequest item : items) {
            Long productId = item.getProductId();
            Integer qty = item.getQuantity();
            merged.merge(productId, qty, Integer::sum);
        }
        return merged;
    }

    private void validateVoucher(Voucher v) {
        if (!v.isActive()) {
            throw new AppException(HttpStatus.BAD_REQUEST, "VOUCHER_INACTIVE", "Voucher is inactive");
        }
        if (v.getExpiresAt() != null && v.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "VOUCHER_EXPIRED", "Voucher is expired");
        }
    }

    private BigDecimal calculateDiscount(BigDecimal total, Voucher v) {
        if (total == null) return BigDecimal.ZERO;
        if (v.getType() == VoucherType.PERCENT) {
            BigDecimal pct = v.getValue();
            return total.multiply(pct).divide(BigDecimal.valueOf(100));
        }
        return v.getValue();
    }

    private BigDecimal clampNonNegative(BigDecimal value) {
        if (value == null) return BigDecimal.ZERO;
        return value.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : value;
    }
}
