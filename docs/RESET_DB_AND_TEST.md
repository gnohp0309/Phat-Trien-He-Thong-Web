# Reset database & test Postman từ đầu

## 1. Reset database (MySQL)

### Cách 1: Xóa database rồi để app tạo lại (đơn giản)

1. Mở **MySQL Workbench** hoặc **HeidiSQL** (hoặc chạy lệnh MySQL).
2. Chạy:

```sql
DROP DATABASE IF EXISTS demo_spmvc;
```

3. **Tắt** ứng dụng Spring Boot (nếu đang chạy).
4. **Chạy lại** ứng dụng Spring Boot.
   - Trong `application.yaml` đang có `createDatabaseIfNotExist=true` nên app sẽ tạo lại database `demo_spmvc` và các bảng trống.

### Cách 2: Chỉ xóa dữ liệu, giữ nguyên bảng

Chạy lần lượt (đúng thứ tự vì có khóa ngoại):

```sql
USE demo_spmvc;

SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE user_roles;
TRUNCATE TABLE app_user;
TRUNCATE TABLE order_detail;
TRUNCATE TABLE orders;
TRUNCATE TABLE product;
TRUNCATE TABLE voucher;
TRUNCATE TABLE customer;

SET FOREIGN_KEY_CHECKS = 1;
```

Sau đó chạy lại app và test Postman.

---

## 2. Test Postman từ đầu (thứ tự gợi ý)

### Lưu ý quan trọng

- **GET** (lấy danh sách, tìm kiếm): **không gửi Body**. Chỉ dùng URL và query (ví dụ `?page=0&size=5&sort=price,desc`).
- **POST** (tạo mới): mới cần Body JSON.
- Mọi API (trừ `/api/auth/**`) đều cần header: **Authorization** = `Bearer <token>`.

### Bước 1 – Auth

1. **POST** `http://localhost:8080/api/auth/register`  
   Body (raw JSON):

   ```json
   { "username": "admin", "password": "123", "admin": true }
   ```

2. **POST** `http://localhost:8080/api/auth/login`  
   Body:

   ```json
   { "username": "admin", "password": "123" }
   ```

   Copy `token` trong response.

3. Trong Postman, tab **Authorization** chọn **Bearer Token** và dán token (hoặc gõ `Bearer <token>` trong header **Authorization**).

### Bước 2 – Product (dùng token ADMIN)

- **GET** `http://localhost:8080/api/products?page=0&size=5&sort=price,desc`  
  → **Không chọn Body**, không gửi JSON.
- **POST** `http://localhost:8080/api/products` (tạo sản phẩm) → lúc này mới cần Body, ví dụ:

  ```json
  { "name": "Bàn phím", "price": 770000, "quantity": 5 }
  ```

### Bước 3 – Customer, Voucher, Order, Report

- Làm theo đúng hướng dẫn test đã gửi trước đó (Customer → Voucher → Order → Report).

---

## 3. Nếu vẫn lỗi 400 với GET /api/products

- Kiểm tra:
  - Đúng URL: `GET http://localhost:8080/api/products?page=0&size=5&sort=price,desc`
  - **Không** chọn Body (để trống) hoặc chọn "none".
- Sau khi sửa code (thêm `name` cho `@RequestParam`), cần **Build lại project** (Build → Rebuild Project trong IntelliJ) rồi chạy lại app.
