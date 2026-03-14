# Hướng dẫn test Postman – Mục 5.3, 6, 7

Base URL: **http://localhost:8080**

Mọi request (trừ login/register) đều cần header:
- **Authorization**: `Bearer <token>`

---

## Chuẩn bị

1. **Token ADMIN**: Login với `admin` / `123` → copy `token`.
2. **Token USER**: Login với `user1` / `123` → copy `token`.
3. Đã có ít nhất **2 order**:
   - Order 1: đã tạo (PENDING), nhớ `id` (ví dụ `1`).
   - Order 2: một order PENDING khác để test cancel (ví dụ `2`).

---

## 5.3 – Test trạng thái đơn + hủy

### PAY (chỉ ADMIN)

| Trường    | Giá trị |
|-----------|---------|
| Method    | **POST** |
| URL       | `http://localhost:8080/api/orders/1/pay` |
| Auth      | Bearer Token → dán **token ADMIN** |
| Body      | Không gửi (none) |

**Kỳ vọng:** Status 200, response JSON có `"status": "PAID"`.

---

### COMPLETE (chỉ ADMIN)

| Trường    | Giá trị |
|-----------|---------|
| Method    | **POST** |
| URL       | `http://localhost:8080/api/orders/1/complete` |
| Auth      | Bearer Token → **token ADMIN** |
| Body      | Không gửi |

**Kỳ vọng:** Status 200, response có `"status": "COMPLETED"`.

---

### CANCEL – Đơn đã PAID/COMPLETED (phải lỗi)

| Trường    | Giá trị |
|-----------|---------|
| Method    | **POST** |
| URL       | `http://localhost:8080/api/orders/1/cancel` |
| Auth      | Bearer Token → **token ADMIN** hoặc **token USER** |
| Body      | Không gửi |

**Kỳ vọng:** Status **400**, body có `"error": "CANNOT_CANCEL"` (hoặc message không cho hủy đơn đã thanh toán/hoàn thành).

---

### CANCEL – Đơn PENDING (thành công + hoàn kho)

Giả sử order id = **2** đang **PENDING** (chưa pay/complete).

| Trường    | Giá trị |
|-----------|---------|
| Method    | **POST** |
| URL       | `http://localhost:8080/api/orders/2/cancel` |
| Auth      | Bearer Token → **token USER** hoặc **token ADMIN** |
| Body      | Không gửi |

**Kỳ vọng:** Status 200, response có `"status": "CANCELLED"`. Kiểm tra DB: số lượng sản phẩm trong order đó được **cộng lại** (hoàn kho).

---

## 6 – Test phân quyền (ADMIN vs USER)

Dùng **token USER** cho tất cả request dưới đây. Kỳ vọng đều **403 Forbidden**.

| Method | URL | Ghi chú |
|--------|-----|--------|
| POST | `http://localhost:8080/api/products` | Body: `{"name":"x","price":1,"quantity":1}` |
| PUT | `http://localhost:8080/api/products/1` | Body: `{"name":"x","price":1,"quantity":1}` |
| DELETE | `http://localhost:8080/api/products/1` | Không body |
| POST | `http://localhost:8080/api/vouchers` | Body: `{"code":"X","type":"PERCENT","value":10}` |
| GET | `http://localhost:8080/api/reports/revenue/daily?from=2026-03-01&toExclusive=2026-04-01` | Không body |
| GET | `http://localhost:8080/api/reports/revenue/monthly?year=2026` | Không body |
| GET | `http://localhost:8080/api/reports/top-products?limit=5` | Không body |

**Kỳ vọng:** Trả về **403** (vì chỉ ADMIN mới được).

---

USER được phép:

| Method | URL | Kỳ vọng |
|--------|-----|--------|
| POST | `http://localhost:8080/api/orders` | 200 (tạo đơn) – Body: `{"customerName":"A","items":[{"productId":1,"quantity":1}]}` |
| POST | `http://localhost:8080/api/orders/2/cancel` | 200 (hủy đơn PENDING) – thay `2` bằng id đơn PENDING |

---

## 7 – Test Report (chỉ ADMIN)

Dùng **token ADMIN**. **GET** không gửi Body.

### Doanh thu theo ngày

| Trường | Giá trị |
|--------|---------|
| Method | **GET** |
| URL | `http://localhost:8080/api/reports/revenue/daily?from=2026-03-01&toExclusive=2026-04-01` |
| Auth | Bearer Token → **token ADMIN** |
| Body | Không chọn / none |

**Kỳ vọng:** 200, body là mảng JSON, mỗi phần tử có dạng `date`, `revenue` (RevenueByDayResponse).

---

### Doanh thu theo tháng

| Trường | Giá trị |
|--------|---------|
| Method | **GET** |
| URL | `http://localhost:8080/api/reports/revenue/monthly?year=2026` |
| Auth | Bearer Token → **token ADMIN** |
| Body | Không |

**Kỳ vọng:** 200, mảng có `year`, `month`, `revenue` (RevenueByMonthResponse).

---

### Top sản phẩm bán chạy

| Trường | Giá trị |
|--------|---------|
| Method | **GET** |
| URL | `http://localhost:8080/api/reports/top-products?limit=5` |
| Auth | Bearer Token → **token ADMIN** |
| Body | Không |

**Kỳ vọng:** 200, mảng có `productId`, `productName`, `totalQuantity` (TopProductResponse).

**Lưu ý:** Report tính trên đơn **COMPLETED**. Nếu chưa có đơn COMPLETED có thể trả về mảng rỗng `[]`.
