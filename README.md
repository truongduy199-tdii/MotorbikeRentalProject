```python
content = """# Hệ Thống Quản Lý Cho Thuê Xe Máy (Motorbike Rental Project)

Một ứng dụng Desktop được phát triển bằng **Java Swing** với kiến trúc **3 lớp (3-Tier Architecture)**, giúp quản lý nghiệp vụ cho thuê xe máy hiệu quả.

## 🚀 Tính năng nổi bật

* **Phân quyền người dùng:** Đăng nhập và phân quyền rõ ràng giữa Quản trị viên (Admin) và Khách hàng (Customer).
* **Quản lý Xe máy:** Thêm, sửa, tìm kiếm xe máy theo từ khóa (mã xe, tên xe, biển số) và lọc theo trạng thái (Đang thuê, Sẵn sàng,...).
* **Quản lý Hợp đồng & Khách hàng:** Theo dõi lịch sử thuê xe, thông tin khách hàng và trạng thái hợp đồng.
* **Thống kê:** Cung cấp các báo cáo và thống kê dành cho quản trị viên.
* **Giao diện hiện đại (UI/UX):** Tích hợp thư viện **FlatLaf** mang lại giao diện sáng sủa, thân thiện và chuyên nghiệp.
* **Bảo mật dữ liệu:** Mật khẩu người dùng được băm (hashing) an toàn bằng thuật toán **SHA-256** trước khi lưu vào cơ sở dữ liệu.

## 🛠 Công nghệ sử dụng

* **Ngôn ngữ lập trình:** Java (JDK 8 trở lên)
* **Giao diện (GUI):** Java Swing + FlatLaf (`flatlaf-3.7.1.jar`)
* **Cơ sở dữ liệu:** MySQL
* **Kết nối CSDL:** JDBC (`mysql-connector-java.jar`)
* **Kiến trúc:** Mô hình 3 lớp (GUI - BUS - DAO)

## 📂 Cấu trúc thư mục dự án


```

```text
Generated README.md

```text
MotorbikeRentalProject/
│
├── database/          # File script SQL khởi tạo CSDL (database.sql)
├── lib/               # Chứa các thư viện bổ sung (.jar)
├── resources/         # Tài nguyên tĩnh (Hình ảnh logo, cấu hình db.properties)
└── src/               # Mã nguồn Java
    ├── bus/           # Business Logic Layer (Kiểm tra dữ liệu, xử lý nghiệp vụ)
    ├── dao/           # Data Access Object Layer (Tương tác CSDL qua MySQLConnect)
    ├── dto/           # Data Transfer Object (Các lớp thực thể như XeMayDTO, TaiKhoanDTO)
    ├── gui/           # Tầng giao diện người dùng
    │   ├── admin/     # Giao diện dành riêng cho Admin
    │   ├── customer/  # Giao diện dành riêng cho Khách hàng
    │   └── common/    # Giao diện chung (Đăng nhập, Đăng ký)
    ├── main/          # Chứa class Main (Điểm bắt đầu khởi chạy ứng dụng)
    └── utils/         # Các tiện ích dùng chung (SecurityHelper, SessionUser)

```

## ⚙️ Hướng dẫn Cài đặt & Chạy dự án

### 1. Chuẩn bị Cơ sở dữ liệu

* Cài đặt hệ quản trị cơ sở dữ liệu MySQL.
* Mở trình quản lý cơ sở dữ liệu (ví dụ: MySQL Workbench, DBeaver) và chạy file script `database/database.sql` để khởi tạo các bảng và dữ liệu mẫu.

### 2. Cấu hình kết nối CSDL

* Mở file `resources/db.properties`.
* Cập nhật lại thông tin kết nối cho phù hợp với môi trường local của bạn:

```properties
db.url=jdbc:mysql://localhost:3306/ten_database
db.username=root
db.password=mat_khau_cua_ban

```

### 3. Cấu hình Thư viện (Libraries)

* Dự án sử dụng các thư viện ngoài. Hãy đảm bảo bạn đã thêm toàn bộ các file `.jar` nằm trong thư mục `lib/` vào **Build Path** (hoặc Project Structure) của IDE (IntelliJ IDEA, Eclipse, NetBeans,...).

### 4. Khởi chạy

* Chạy class `src/main/Main.java` để khởi động ứng dụng.
* Đăng nhập bằng tài khoản Admin hoặc Customer có sẵn trong CSDL để trải nghiệm.
  """


* Tài khoản Admin mẫu:
  - Username: admin
  - Password: 123
  

* Tài khoản Customer mẫu:
  - Username: 0901666124
  - password: 1234