-- Create Database
CREATE SCHEMA IF NOT EXISTS pbi1;
USE pbi1;

-- Drop table (เผื่อเคยสร้างแล้ว)
DROP TABLE IF EXISTS SaleItem;
DROP TABLE IF EXISTS Brand;

-- CREATE TABLE Brand
CREATE TABLE Brand (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL CHECK (TRIM(name) <> ''),
    websiteUrl VARCHAR(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL CHECK (websiteUrl IS NULL OR TRIM(websiteUrl) <> ''),
    isActive BOOLEAN NOT NULL DEFAULT TRUE,
    countryOfOrigin VARCHAR(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL CHECK (countryOfOrigin IS NULL OR TRIM(countryOfOrigin) <> ''),
    createdOn DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedOn DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- CREATE TABLE SaleItem
CREATE TABLE SaleItem (
    id INT AUTO_INCREMENT PRIMARY KEY,
    model VARCHAR(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL CHECK (TRIM(model) <> ''),
    brand_id INT,
    description TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL CHECK (TRIM(description) <> ''),
    price INT NOT NULL CHECK (price >= 0),
    ramGb INT DEFAULT NULL CHECK (ramGb IS NULL OR ramGb >= 0),
    screenSizeInch DECIMAL(3,1) DEFAULT NULL,
    storageGb INT DEFAULT NULL,
    color VARCHAR(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL CHECK (color IS NULL OR TRIM(color) <> ''),
    quantity INT NOT NULL DEFAULT 1,
    createdOn DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedOn DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (brand_id) REFERENCES brand(id)
);

-- insert ข้อมูลลงใน  Table Brand
INSERT INTO Brand (name, countryOfOrigin, websiteUrl, isActive) VALUES
('Samsung', 'South Korea', 'https://www.samsung.com', TRUE),
('Apple', 'United States', 'https://www.apple.com', TRUE),
('Xiaomi', 'China', 'https://www.mi.com', TRUE),
('Huawei', 'China', 'https://www.huawei.com', TRUE),
('OnePlus', 'China', 'https://www.oneplus.com', TRUE),
('Sony', 'Japan', 'https://www.sony.com', TRUE),
('LG', 'South Korea', 'https://www.lg.com', TRUE),
('Nokia', 'Finland', 'https://www.nokia.com', FALSE),
('Motorola', 'United States', 'https://www.motorola.com', FALSE),
('OPPO', 'China', 'https://www.oppo.com', TRUE),
('Vivo', 'China', 'https://www.vivo.com', TRUE),
('ASUS', 'Taiwan', 'https://www.asus.com', TRUE),
('Google', 'United States', 'https://store.google.com', TRUE),
('Realme', 'China', 'https://www.realme.com', TRUE),
('BlackBerry', 'Canada', 'https://www.blackberry.com', TRUE),
('HTC', 'Taiwan', 'https://www.htc.com', TRUE),
('ZTE', 'China', 'https://www.zte.com', TRUE),
('Lenovo', 'China', 'https://www.lenovo.com', TRUE),
('Honor', 'China', 'https://www.hihonor.com', TRUE),
('Nothing', 'United Kingdom', 'https://nothing.tech', TRUE);

-- แสดงข้อมูลใน Table Brand
SELECT * FROM pbi1.brand;
