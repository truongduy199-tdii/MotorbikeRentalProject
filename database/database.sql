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
                       status ENUM('ACTIVE', 'BLOCKED') DEFAULT 'ACTIVE'
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
                          status ENUM('AVAILABLE', 'RENTED', 'MAINTENANCE', 'DELETED') DEFAULT 'AVAILABLE',
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
                                  deposit_amount DECIMAL(10,2) DEFAULT 0 CHECK (deposit_amount >= 0),
                                  total_amount DECIMAL(10,2) DEFAULT 0 CHECK (total_amount >= 0),
                                  contract_status ENUM('PENDING', 'ACTIVE', 'COMPLETED', 'CANCELLED') DEFAULT 'PENDING',
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
                          payment_method ENUM('BANKING') NOT NULL,
                          payment_amount DECIMAL(10,2) NOT NULL CHECK (payment_amount >= 0),
                          payment_status ENUM('PAID', 'UNPAID') DEFAULT 'UNPAID',
                          transaction_code VARCHAR(50) UNIQUE,
                          FOREIGN KEY (contract_id) REFERENCES RENTAL_CONTRACTS(contract_id) ON DELETE CASCADE
);


-- PART 2: SAMPLE DATA INSERTION (DML)

-- 1. INSERT DATA: USERS
INSERT INTO USERS (username, password_hash, role)
VALUES
    ('admin', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'ADMIN'),
    ('0901666124', '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4', 'CUSTOMER'),
    ('0986164589', '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4', 'CUSTOMER'),
    ('0903484322', '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4', 'CUSTOMER'),
    ('0904498463', '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4', 'CUSTOMER'),
    ('0912345678', '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4', 'CUSTOMER'),
    ('0987654321', '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4', 'CUSTOMER'),
    ('0933445566', '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4', 'CUSTOMER'),
    ('0977889900', '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4', 'CUSTOMER'),
    ('0909112233', '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4', 'CUSTOMER'),
    ('0944556677', '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4', 'CUSTOMER');

-- 2. INSERT DATA: CUSTOMERS
INSERT INTO CUSTOMERS (user_id, full_name, phone, email, cccd, birthday, address, driver_license_number)
VALUES
    (2, 'Nguyen Van A', '0901666124', 'nva@gmail.com', '048099123456', '1999-01-01', 'Ngu Hanh Son, Da Nang', 'GPLX123456'),
    (3, 'Tran Thi B', '0986164589', 'ttb@gmail.com', '048199234567', '2000-05-15', 'Hai Chau, Da Nang', 'GPLX234567'),
    (4, 'Le Van C', '0903484322', 'lvc@gmail.com', '048299345678', '1998-10-20', 'Lien Chieu, Da Nang', 'GPLX345678'),
    (5, 'Pham Thi D', '0904498463', 'ptd@gmail.com', '048399456789', '2002-12-05', 'Son Tra, Da Nang', 'GPLX456789'),
    (6, 'Hoang Van E', '0912345678', 'hve@gmail.com', '048499567890', '1995-03-12', 'Thanh Khe, Da Nang', 'GPLX567890'),
    (7, 'Ngo Thi F', '0987654321', 'ntf@gmail.com', '048599678901', '2001-08-25', 'Cam Le, Da Nang', 'GPLX678901'),
    (8, 'Vu Van G', '0933445566', 'vvg@gmail.com', '048699789012', '1997-11-03', 'Hoa Vang, Da Nang', 'GPLX789012'),
    (9, 'Bui Thi H', '0977889900', 'bth@gmail.com', '048799890123', '1996-07-19', 'Ngu Hanh Son, Da Nang', 'GPLX890123'),
    (10, 'Dang Van K', '0909112233', 'dvk@gmail.com', '048899901234', '1994-02-28', 'Hai Chau, Da Nang', 'GPLX901234'),
    (11, 'Ly Thi L', '0944556677', 'ltl@gmail.com', '048999012345', '2003-09-09', 'Lien Chieu, Da Nang', 'GPLX012345');

-- 3. INSERT DATA: VEHICLES
INSERT INTO VEHICLES (vehicle_code, brand, model, license_plate, color, manufacture_year, rental_price_per_day, status)
VALUES
    ('XM001', 'Honda', 'AirBlade 125', '43D1-123.45', 'Đen', 2022, 150000, 'AVAILABLE'),
    ('XM002', 'Yamaha', 'Exciter 150', '43D1-678.90', 'Xanh', 2021, 200000, 'AVAILABLE'),
    ('XM003', 'Honda', 'Vision 2023', '43D1-222.33', 'Trắng', 2023, 120000, 'AVAILABLE'),
    ('XM004', 'Honda', 'Wave Alpha', '43D1-444.55', 'Xanh', 2021, 100000, 'RENTED'),
    ('XM005', 'Honda', 'SH 150i', '43D1-666.77', 'Đen', 2022, 300000, 'RENTED'),
    ('XM006', 'Yamaha', 'Sirius', '43D1-888.99', 'Đỏ Đen', 2020, 100000, 'MAINTENANCE'),
    ('XM007', 'Honda', 'Lead 125', '43D1-111.22', 'Vàng', 2021, 150000, 'RENTED'),
    ('XM008', 'Honda', 'RSX', '43D1-668.68', 'Trắng', 2024, 150000, 'DELETED'),
    ('XM009', 'Piaggio', 'Vespa Sprint', '43D1-901.12', 'Cam', 2023, 250000, 'AVAILABLE'),
    ('XM010', 'Suzuki', 'Raider 150', '43D1-234.56', 'Xanh Đen', 2021, 180000, 'RENTED'),
    ('XM011', 'Honda', 'Winner X', '43D1-345.67', 'Đỏ Đen', 2022, 200000, 'AVAILABLE'),
    ('XM012', 'Yamaha', 'Grande', '43D1-456.78', 'Trắng', 2022, 160000, 'RENTED'),
    ('XM013', 'Honda', 'SH Mode', '43D1-567.89', 'Bạc', 2021, 220000, 'MAINTENANCE'),
    ('XM014', 'Yamaha', 'NVX 155', '43D1-678.91', 'Xanh GP', 2020, 180000, 'AVAILABLE'),
    ('XM015', 'Honda', 'PCX 150', '43D1-789.12', 'Đen Mờ', 2019, 250000, 'RENTED'),
    ('XM016', 'Suzuki', 'Burgman', '43D1-890.23', 'Đen', 2022, 200000, 'AVAILABLE'),
    ('XM017', 'Piaggio', 'Liberty', '43D1-901.34', 'Trắng', 2021, 180000, 'RENTED'),
    ('XM018', 'Honda', 'Vision 2022', '43D1-012.45', 'Đỏ', 2022, 120000, 'AVAILABLE'),
    ('XM019', 'Yamaha', 'Janus', '43D1-123.56', 'Hồng', 2023, 130000, 'AVAILABLE'),
    ('XM020', 'Honda', 'Future 125FI', '43D1-999.88', 'Xanh Xám', 2024, 150000, 'DELETED');

-- 4. INSERT DATA: RENTAL_CONTRACTS
INSERT INTO RENTAL_CONTRACTS (contract_code, customer_id, vehicle_id, created_by, rental_start, rental_end, actual_return_time, deposit_amount, total_amount, contract_status)
VALUES
    ('HD01', 1, 1, 1, '2026-04-20 08:00:00', '2026-04-22 10:00:00', '2026-04-22 07:30:00', 1000000, 1450000, 'COMPLETED'),
    ('HD02', 2, 4, 1, '2026-05-10 09:00:00', '2026-05-17 10:00:00', NULL, 1000000, 1800000, 'ACTIVE'),
    ('HD03', 3, 7, 1, '2026-05-07 10:00:00', '2026-05-16 16:00:00', NULL, 1000000, 2500000, 'ACTIVE'),
    ('HD04', 4, 5, 1, '2026-05-13 11:00:00', '2026-05-19 16:00:00', NULL, 1000000, 3100000, 'ACTIVE'),
    ('HD05', 5, 10, 1, '2026-05-14 08:00:00', '2026-05-18 16:00:00', NULL, 1000000, 1900000, 'ACTIVE'),
    ('HD06', 6, 12, 1, '2026-05-15 09:30:00', '2026-05-20 10:00:00', NULL, 1000000, 1960000, 'ACTIVE'),
    ('HD07', 7, 15, 1, '2026-05-15 14:00:00', '2026-05-17 16:00:00', NULL, 1000000, 1750000, 'ACTIVE'),
    ('HD08', 8, 17, 1, '2026-05-12 10:00:00', '2026-05-16 10:00:00', NULL, 1000000, 1900000, 'ACTIVE'),
    ('HD09', 9, 9, 1, '2026-04-01 07:00:00', '2026-04-05 10:00:00', '2026-04-05 08:00:00', 1000000, 2250000, 'COMPLETED'),
    ('HD10', 10, 11, 1, '2026-04-10 13:00:00', '2026-04-12 16:00:00', '2026-04-12 12:30:00', 1000000, 1600000, 'COMPLETED'),
    ('HD14', 4, 18, 1, '2026-05-20 08:00:00', '2026-05-25 10:00:00', NULL, 0, 0, 'CANCELLED');

-- 5. INSERT DATA: PAYMENTS
INSERT INTO PAYMENTS (contract_id, payment_method, payment_amount, payment_status, transaction_code)
VALUES
    (1, 'BANKING', 1450000, 'PAID', 'VN176835748'),
    (2, 'BANKING', 1800000, 'PAID', 'VN839364638'),
    (3, 'BANKING', 2500000, 'PAID', 'VN123456789'),
    (4, 'BANKING', 3100000, 'PAID', 'VN987654321'),
    (5, 'BANKING', 1900000, 'UNPAID', NULL),
    (6, 'BANKING', 1960000, 'PAID', 'VN22334455'),
    (7, 'BANKING', 1750000, 'PAID', 'VN33445566'),
    (8, 'BANKING', 1900000, 'UNPAID', NULL),
    (9, 'BANKING', 2250000, 'PAID', 'VN55667788'),
    (10, 'BANKING', 1600000, 'PAID', NULL);