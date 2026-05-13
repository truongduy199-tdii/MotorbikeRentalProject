DROP DATABASE IF EXISTS motorbikeDB;

CREATE DATABASE motorbikeDB;
USE motorbikeDB;

-- PART 1: TABLES DEFINITION (DDL)
-- 1. TABLE: USERS
CREATE TABLE USERS (
                       user_id INT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       role ENUM('ADMIN', 'CUSTOMER') NOT NULL,
                       status ENUM('ACTIVE', 'BLOCKED') DEFAULT 'ACTIVE',
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. TABLE: CUSTOMERS
CREATE TABLE CUSTOMERS (
                           customer_id INT AUTO_INCREMENT PRIMARY KEY,
                           user_id INT UNIQUE NOT NULL, -- 1-1 với TK
                           full_name VARCHAR(100) NOT NULL,
                           phone VARCHAR(10) NOT NULL UNIQUE,
                           email VARCHAR(100),
                           cccd VARCHAR(12) NOT NULL UNIQUE,
                           birthday DATE,
                           address VARCHAR(255),
                           driver_license_number VARCHAR(20),
                           status ENUM('ACTIVE', 'BLOCKED') DEFAULT 'ACTIVE',
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           FOREIGN KEY (user_id) REFERENCES USERS(user_id) ON DELETE CASCADE
);

-- 3. TABLE: VEHICLES
CREATE TABLE VEHICLES (
                          vehicle_id INT AUTO_INCREMENT PRIMARY KEY,
                          vehicle_code VARCHAR(20) NOT NULL UNIQUE,
                          brand VARCHAR(50) NOT NULL,
                          model VARCHAR(50) NOT NULL,
                          license_plate VARCHAR(20) NOT NULL UNIQUE,
                          color VARCHAR(30),
                          manufacture_year INT CHECK (manufacture_year >= 2000),
                          rental_price_per_day DECIMAL(10,2) NOT NULL CHECK (rental_price_per_day > 0),
                          rental_price_per_hour DECIMAL(10,2) NOT NULL CHECK (rental_price_per_hour > 0),
                          status ENUM('AVAILABLE', 'RENTED', 'MAINTENANCE', 'INACTIVE') DEFAULT 'AVAILABLE',
                          description TEXT,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          INDEX idx_vehicles_plate (license_plate),
                          INDEX idx_vehicles_status (status)
);

-- 4. TABLE: RENTAL_CONTRACTS
CREATE TABLE RENTAL_CONTRACTS (
                                  contract_id INT AUTO_INCREMENT PRIMARY KEY,
                                  contract_code VARCHAR(20) NOT NULL UNIQUE,
                                  customer_id INT NOT NULL,
                                  vehicle_id INT NOT NULL,
                                  created_by INT NOT NULL,
                                  rental_start DATETIME NOT NULL,
                                  rental_end DATETIME NOT NULL,
                                  actual_return_time DATETIME NULL,
                                  rental_type ENUM('HOUR', 'DAY') NOT NULL,
                                  deposit_amount DECIMAL(10,2) DEFAULT 0 CHECK (deposit_amount >= 0),
                                  total_amount DECIMAL(10,2) DEFAULT 0 CHECK (total_amount >= 0),
                                  contract_status ENUM('PENDING', 'ACTIVE', 'COMPLETED', 'CANCELLED') DEFAULT 'PENDING',
                                  note TEXT,
                                  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                  CHECK (rental_end > rental_start),
                                  FOREIGN KEY (customer_id) REFERENCES CUSTOMERS(customer_id) ON DELETE RESTRICT,
                                  FOREIGN KEY (vehicle_id) REFERENCES VEHICLES(vehicle_id) ON DELETE RESTRICT,
                                  FOREIGN KEY (created_by) REFERENCES USERS(user_id) ON DELETE RESTRICT,
                                  INDEX idx_contracts_status (contract_status),
                                  INDEX idx_contracts_customer (customer_id),
                                  INDEX idx_contracts_vehicle (vehicle_id)
);

-- 5. TABLE: PAYMENTS
CREATE TABLE PAYMENTS (
                          payment_id INT AUTO_INCREMENT PRIMARY KEY,
                          contract_id INT NOT NULL,
                          payment_method ENUM('CASH', 'BANKING') NOT NULL,
                          payment_amount DECIMAL(10,2) NOT NULL CHECK (payment_amount >= 0),
                          payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          payment_status ENUM('PAID', 'UNPAID', 'REFUNDED') DEFAULT 'PAID',
                          transaction_code VARCHAR(50) UNIQUE,
                          note TEXT,
                          FOREIGN KEY (contract_id) REFERENCES RENTAL_CONTRACTS(contract_id) ON DELETE CASCADE
);


-- PART 2: SAMPLE DATA INSERTION (DML)

-- 1. INSERT DATA: USERS
INSERT INTO USERS (username, password_hash, role)
VALUES
    ('admin', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'ADMIN'),
    ('0901666124', 'f9573f8ad701007c2217bb602521a6b1fa9e3e52ef2cb84b95618f45650efd61', 'CUSTOMER'),
    ('0986164589', 'f2ff4cea9ca5162e631d6fec17b6e36d8a455bcb1ed163a2058d9ce136b210c1', 'CUSTOMER'),
    ('0903484322', '6fa304a68ae214a426f9850bfc1791e61156be1db1ee4534d2041d320e645245', 'CUSTOMER'),
    ('0904498463', 'f464c8bc9935f6d43c8c76fa9b29ffad657c5badc498eb114178ece3c8f4a291', 'CUSTOMER');

-- 2. INSERT DATA: CUSTOMERS
INSERT INTO CUSTOMERS (user_id, full_name, phone, email, cccd, birthday, address, driver_license_number)
VALUES
    (2, 'Nguyen Van A', '0901666124', 'nva@gmail.com', '048099123456', '1999-01-01', 'Ngu Hanh Son, Da Nang', 'GPLX123456'),
    (3, 'Tran Thi B', '0986164589', 'ttb@gmail.com', '048199234567', '2000-05-15', 'Hai Chau, Da Nang', 'GPLX234567'),
    (4, 'Le Van C', '0903484322', 'lvc@gmail.com', '048299345678', '1998-10-20', 'Lien Chieu, Da Nang', 'GPLX345678'),
    (5, 'Pham Thi D', '0904498463', 'ptd@gmail.com', '048399456789', '2002-12-05', 'Son Tra, Da Nang', 'GPLX456789');

-- 3. INSERT DATA: VEHICLES
INSERT INTO VEHICLES (vehicle_code, brand, model, license_plate, color, manufacture_year, rental_price_per_day, rental_price_per_hour, status)
VALUES
    ('XM001', 'Honda', 'AirBlade 125', '43D1-123.45', 'Đen', 2022, 150000, 20000, 'AVAILABLE'),
    ('XM002', 'Yamaha', 'Exciter 150', '43D1-678.90', 'Xanh', 2021, 200000, 30000, 'AVAILABLE'),
    ('XM003', 'Honda', 'Vision 2023', '43D1-222.33', 'Trắng', 2023, 120000, 15000, 'AVAILABLE'),
    ('XM004', 'Honda', 'Wave Alpha', '43D1-444.55', 'Xanh', 2021, 100000, 10000, 'RENTED'),
    ('XM005', 'Honda', 'SH 150i', '43D1-666.77', 'Đen', 2022, 300000, 40000, 'AVAILABLE'),
    ('XM006', 'Yamaha', 'Sirius', '43D1-888.99', 'Đỏ Đen', 2020, 100000, 10000, 'MAINTENANCE'),
    ('XM007', 'Honda', 'Lead 125', '43D1-111.22', 'Vàng', 2021, 150000, 20000, 'RENTED');

-- 4. INSERT DATA: RENTAL_CONTRACTS
INSERT INTO RENTAL_CONTRACTS (contract_code, customer_id, vehicle_id, created_by, rental_start, rental_end, actual_return_time, rental_type, deposit_amount, total_amount, contract_status, note)
VALUES
    ('HD260501', 1, 1, 1, '2026-04-20 08:00:00', '2026-04-22 08:00:00', '2026-04-22 07:30:00', 'DAY', 500000, 300000, 'COMPLETED', 'Khách trả xe đúng giờ, xe nguyên vẹn'),
    ('HD260502', 2, 4, 1, '2026-05-05 09:00:00', '2026-05-10 09:00:00', NULL, 'DAY', 1000000, 500000, 'ACTIVE', 'Khách thuê dài ngày đi phượt'),
    ('HD260503', 3, 7, 1, '2026-05-07 14:00:00', '2026-05-08 14:00:00', NULL, 'DAY', 500000, 150000, 'ACTIVE', 'Giao xe tận nơi cho khách'),
    ('HD260504', 4, 5, 1, '2026-05-15 10:00:00', '2026-05-15 15:00:00', NULL, 'HOUR', 1000000, 200000, 'PENDING', 'Khách đã cọc tiền, chờ ngày lấy xe');

-- 5. INSERT DATA: PAYMENTS
INSERT INTO PAYMENTS (contract_id, payment_method, payment_amount, payment_status, transaction_code, note)
VALUES
    (1, 'CASH', 300000, 'PAID', NULL, 'Thu tiền mặt khi thanh lý hợp đồng HD260501'),
    (2, 'BANKING', 500000, 'UNPAID', NULL, 'Chờ khách thanh toán khi trả xe'),
    (3, 'BANKING', 150000, 'PAID', 'VN123456789', 'Khách chuyển khoản trước 100% qua ngân hàng'),
    (4, 'BANKING', 1000000, 'PAID', 'VN987654321', 'Nhận tiền cọc giữ xe SH');