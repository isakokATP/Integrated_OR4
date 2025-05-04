CREATE USER  'user'@'%' IDENTIFIED BY 'mysql';
GRANT ALL ON *.* TO 'user'@'%';
CREATE SCHEMA IF NOT EXISTS pbi1;
USE pbi1;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS sale_items;
DROP TABLE IF EXISTS brands;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE brands (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL CHECK (TRIM(name) <> '') ,
                        website_url VARCHAR(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL CHECK (TRIM(website_url) IS NULL OR TRIM(website_url) <> '')  ,
                        is_active BOOLEAN NOT NULL DEFAULT TRUE,
                        country_of_origin VARCHAR(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL CHECK (TRIM(country_of_origin) IS NULL OR TRIM(country_of_origin) <> ''),
                        created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE sale_items (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            model VARCHAR(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL CHECK (TRIM(model) <> ''),
                            brand_id INT,
                            description TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL CHECK (TRIM(description) <> ''),
                            price INT NOT NULL CHECK (price >= 0),
                            ram_gb INT DEFAULT NULL CHECK (ram_gb IS NULL OR ram_gb >= 0),
                            screen_size_inch DECIMAL(3,1) DEFAULT NULL,
                            storage_gb INT DEFAULT NULL,
                            color VARCHAR(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL CHECK (color IS NULL OR TRIM(color) <> ''),
                            quantity INT NOT NULL DEFAULT 1,
                            created_on DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            updated_on DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            FOREIGN KEY (brand_id) REFERENCES brands(id)
);

INSERT INTO brands (name, country_of_origin, website_url, is_active) VALUES
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

INSERT INTO sale_items
(model, brand_id, description, price, ram_gb, screen_size_inch, storage_gb, color, quantity, created_on, updated_on)
VALUES
    ('Galaxy S24', 1, 'Newest Samsung model', 30000, 8, 6.8, 256, 'Phantom Black', 5, NOW(), NOW()),
    ('iPhone 15', 2, 'Latest iPhone model', 42000, 6, 6.1, 128, 'Silver', 3, NOW(), NOW());


SELECT * FROM sale_items WHERE brand_id IS NULL;

SELECT * FROM brands;