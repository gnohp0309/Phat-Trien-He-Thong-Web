**Xây dựng Hệ thống Quản lý Sản phẩm & Đơn hàng** 

(Sử dụng Spring Boot – MVC – RESTful API) 

1. **Mục tiêu** 

Sinh viên cần xây dựng một ứng dụng Backend sử dụng: 

1. Spring Boot 
1. Mô hình MVC 
1. Spring Data JPA 
1. RESTful API 
1. MySQL hoặc H2 Database Mục tiêu: 
1. Hiểu rõ mô hình MVC 
1. Thiết kế Entity & Quan hệ bảng 
1. Xây dựng API theo chuẩn REST 
1. Sử dụng DTO và tách tầng Service 
1. Xử lý logic nghiệp vụ (Business Logic) 
1. Quản lý Transaction 
1. Validation dữ liệu 
1. Xử lý Exception tập trung 
1. **Yêu cầu chức năng hệ thống** Hệ thống gồm 3 bảng chính: 
2. **1.Product (Sản phẩm)** Thông tin: 
   - id (Long) 
   - name (String) 
   - price (BigDecimal) 
   - quantity (Integer) **Yêu cầu API:** 
   - Thêm sản phẩm 
   - Lấy danh sách sản phẩm 
   - Xóa sản phẩm **2.2. Order (Đơn hàng)** Thông tin: 
   - id (Long) 
   - customerName (String) 
   - orderDate (LocalDateTime) 
   - totalAmount (BigDecimal) 

**2.3. OrderDetail (Chi tiết đơn hàng)** Thông tin: 

1. id (Long) 
1. product (ManyToOne) 
1. order (ManyToOne) 
1. quantity (Integer) 
1. price (BigDecimal) 
1. subTotal (BigDecimal) 
2. **Quan hệ giữa các bảng** 
   - 1 Order có nhiều OrderDetail 
   - 1 Product có nhiều OrderDetail 
   - OrderDetail là bảng trung gian Sinh viên phải sử dụng: 
   - @OneToMany 
   - @ManyToOne 
   - Cascade hợp lý 
   - @Transactional **4. Logic nghiệp vụ bắt buộc** Khi tạo Order: 
   1. Kiểm tra sản phẩm tồn tại 
   1. Kiểm tra đủ số lượng tồn kho 
   1. Tính subTotal = price × quantity 
   1. Tính totalAmount = tổng các subTotal 
   1. Trừ số lượng tồn kho của Product 
   1. Lưu Order và OrderDetail trong cùng transaction Nếu: 
   - Không đủ hàng → báo lỗi 
   - Không tìm thấy sản phẩm → báo lỗi 

**5. Yêu cầu kỹ thuật** Sinh viên phải: 

- Tách đầy đủ các tầng: o Controller o Service o Repository o Entity o DTO 

`	`o 	Mapper 

- Không trả trực tiếp Entity ra ngoài 
- Phải sử dụng DTO 
- Sử dụng @Valid và Validation 
- Có Global Exception Handler **6. Cấu trúc project yêu cầu** controller/ service/ service/impl/ repository/ entity/ dto/ mapper/ exception/ 

**7. API bắt buộc phải có** 

**Product API** 

|**Method URL** |**Chức năng** |
| :- | :- |
|POST 	/api/products |Tạo sản phẩm |
|GET 	/api/products |Lấy danh sách |

DELETE /api/products/{id} Xóa 

**Order API** 

**Method URL 	Chức năng** 

POST 	/api/orders Tạo đơn hàng 


**\


**CÁC YÊU CẦU MỞ RỘNG** 

**1. Cập nhật & Tìm kiếm sản phẩm Yêu cầu:** 

- PUT /api/products/{id} 
- Tìm theo: 
  - Tên (LIKE) 
  - Khoảng giá (minPrice – maxPrice) o 	Còn hàng / hết hàng **Kiến thức sử dụng:** 
- Spring Data JPA Query Method 
- @Query 
- Pageable **2. Phân trang (Pagination) Yêu cầu:** 
- GET /api/products?page=0&size=5&sort=price,desc Sinh viên phải: 
- Trả về PageResponse DTO 
- Không trả trực tiếp Page<Entity> 
3. **Trạng thái đơn hàng** Thêm field: 

OrderStatus { PENDING, PAID, CANCELLED, COMPLETED } Yêu cầu: 

1. Tạo đơn → PENDING 
1. API xác nhận thanh toán → PAID 
1. API hủy đơn → CANCELLED 
1. Không được hủy nếu đã COMPLETED 
3. **Hủy đơn và hoàn trả tồn kho** Khi hủy đơn: 
   1. Trả lại stock cho Product 
   1. Không được hủy nếu đã thanh toán Yêu cầu sử dụng: 
   1. Transaction 
   1. Optimistic Lock 
3. **Thêm Discount / Voucher** 
   1. Giảm theo % 
   1. Giảm theo số tiền cố định 
   1. Có ngày hết hạn Yêu cầu: 
   1. Tính lại totalAmount 
   1. Không cho âm tiền 
3. **Thêm thông tin khách hàng riêng (Customer table)** Quan hệ: 

Customer (1) --- (n) Order Yêu cầu: 

1. Lưu lịch sử đơn hàng 
1. API xem đơn theo khách hàng 
3. **Thêm Authentication (JWT)** Yêu cầu: 
   1. Đăng ký 
   1. Đăng nhập 
   1. Role: 

o 	ADMIN o 	USER Chỉ ADMIN: 

1. Tạo sản phẩm 
1. Xóa sản phẩm USER: 
1. Tạo đơn **8. Phân quyền API** Sử dụng: 

@PreAuthorize("hasRole('ADMIN')") **9. Kiểm tra trùng sản phẩm trong Order** Nếu request: 

[ 

` `{ "productId": 1, "quantity": 2 }, 

` `{ "productId": 1, "quantity": 3 } 

] 

→ Phải gộp lại thành 5. 

10. **Không cho xóa Product nếu đã có Order** Yêu cầu: 
    1. Kiểm tra tồn tại OrderDetail 
    1. Throw exception 
10. **Báo cáo thống kê** API: 
    1. Tổng doanh thu theo ngày 
    1. Tổng doanh thu theo tháng 
    1. Top 5 sản phẩm bán chạy Yêu cầu: 
    1. JPQL 
    1. Group By 
    1. Projection DTO 
10. **Soft Delete** 

Không xóa thật Product: 

1. Thêm field deleted = true 
1. Query chỉ lấy deleted = false 


