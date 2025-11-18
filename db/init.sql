SET NAMES utf8mb4;

CREATE USER  'user'@'%' IDENTIFIED BY 'mysql';
GRANT ALL ON *.* TO 'user'@'%';
CREATE SCHEMA IF NOT EXISTS pbi1;
USE pbi1;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS cart_items;
DROP TABLE IF EXISTS attachments;
DROP TABLE IF EXISTS sale_items;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS brands;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE users (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       nick_name VARCHAR(50) NOT NULL,
                       email VARCHAR(150) NOT NULL UNIQUE,
                       full_name VARCHAR(150) NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       phone_number VARCHAR(15) DEFAULT NULL,
                       bank_name VARCHAR(150) DEFAULT NULL,
                       bank_account VARCHAR(30) DEFAULT NULL,
                       id_card_number VARCHAR(20) DEFAULT NULL,
                       user_type ENUM('SELLER', 'BUYER') NOT NULL,
                       id_card_image_front VARCHAR(255) DEFAULT NULL,
                       id_card_image_back VARCHAR(255) DEFAULT NULL,
                       status ENUM('INACTIVE','ACTIVE') NOT NULL DEFAULT 'INACTIVE',
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO users (
    id,
    user_type,
    nick_name,
    email,
    password,
    full_name,
    phone_number,
    bank_account,
    bank_name,
    id_card_number,
    id_card_image_front,
    id_card_image_back,
    status
) VALUES
      (1, 'BUYER', 'Somchai', 'itbkk.somchai@ad.sit.kmutt.ac.th', '$argon2d$v=19$m=16,t=2,p=1$c0dyOGNYQWV4eXNhRTNRbg$9FFZoEIVUMDFGH9or9orAw', 'Somchai Jaidee',
       NULL, NULL, NULL, NULL, NULL, NULL, 'ACTIVE'),

      (2, 'BUYER', 'Somkiat', 'itbkk.somkiat@ad.sit.kmutt.ac.th', '$argon2d$v=19$m=16,t=2,p=1$c0dyOGNYQWV4eXNhRTNRbg$9FFZoEIVUMDFGH9or9orAw', 'Somkiat Luckchart',
       NULL, NULL, NULL, NULL, NULL, NULL, 'ACTIVE'),

      (3, 'SELLER', 'Somsuan', 'itbkk.somsuan@ad.sit.kmutt.ac.th', '$argon2d$v=19$m=16,t=2,p=1$c0dyOGNYQWV4eXNhRTNRbg$9FFZoEIVUMDFGH9or9orAw', 'Somsuan Hundee',
       '0834567890', '0371234567', 'Bangkok Bank', '1000111100222',
       '1000111100222_front.png', '1000111100222_back.png', 'ACTIVE'),

      (4, 'SELLER', 'Somsuk', 'itbkk.somsuk@ad.sit.kmutt.ac.th', '$argon2d$v=19$m=16,t=2,p=1$c0dyOGNYQWV4eXNhRTNRbg$9FFZoEIVUMDFGH9or9orAw', 'Somsuk Fundee',
       '0845678901', '2371234567', 'Siam Commercial Bank', '1000111100333',
       '1000111100333_front.png', '1000111100333_back.png', 'ACTIVE'),

      (5, 'SELLER', 'Somsak', 'itbkk.somsak@ad.sit.kmutt.ac.th', '$argon2d$v=19$m=16,t=2,p=1$c0dyOGNYQWV4eXNhRTNRbg$9FFZoEIVUMDFGH9or9orAw', 'Somsak Saksit',
       '0856789012', '0373456789', 'Bangkok Bank', '1000111100444',
       '1000111100444_front.png', '1000111100444_back.png', 'ACTIVE');

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
                            seller_id INT,
                            description TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL CHECK (TRIM(description) <> ''),
                            price INT NOT NULL CHECK (price >= 0),
                            ram_gb INT DEFAULT NULL CHECK (ram_gb IS NULL OR ram_gb >= 0),
                            screen_size_inch DECIMAL(4,2) DEFAULT NULL,
                            storage_gb INT DEFAULT NULL,
                            color VARCHAR(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                            quantity BIGINT NOT NULL DEFAULT 1,
                            created_on DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            updated_on DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            FOREIGN KEY (brand_id) REFERENCES brands(id),
                            FOREIGN KEY (seller_id) REFERENCES users(id)
);

CREATE TABLE cart_items (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            user_id INT NOT NULL,
                            sale_item_id INT NOT NULL,
                            seller_id INT NOT NULL,
                            quantity INT NOT NULL DEFAULT 1 CHECK (quantity > 0),
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                            FOREIGN KEY (sale_item_id) REFERENCES sale_items(id) ON DELETE CASCADE,
                            FOREIGN KEY (seller_id) REFERENCES users(id),

                            UNIQUE KEY uk_user_sale_item (user_id, sale_item_id)
);

CREATE TABLE orders (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        buyer_user_id INT NOT NULL,
                        seller_id INT NOT NULL,
                        order_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        total_items INT DEFAULT NULL,
                        total_price DECIMAL(10, 2) DEFAULT NULL,
                        status ENUM('PENDING', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED', 'COMPLETED') NOT NULL DEFAULT 'PENDING',
                        shipping_address VARCHAR(500) DEFAULT NULL,
                        note VARCHAR(500) DEFAULT NULL,
                        is_viewed BOOLEAN NOT NULL DEFAULT FALSE,

                        FOREIGN KEY (buyer_user_id) REFERENCES users(id),
                        FOREIGN KEY (seller_id) REFERENCES users(id)
);

CREATE TABLE order_items (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             order_id BIGINT NOT NULL,
                             sale_item_id INT NOT NULL,
                             quantity INT NOT NULL CHECK (quantity > 0),
                             price_at_order DECIMAL(10, 2) NOT NULL,

                             FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
                             FOREIGN KEY (sale_item_id) REFERENCES sale_items(id)
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
(id, model, brand_id, seller_id, description, price, ram_gb, screen_size_inch, storage_gb, color, quantity, created_on, updated_on)
VALUES
    (1, 'iPhone 14 Pro Max', 2, 3, 'ไอโฟนเรือธงรุ่นล่าสุด มาพร้อม Dynamic Island จอใหญ่สุดในตระกูล กล้องระดับโปร', 42900, 6, 6.7, 512, 'Space Black', 5, NOW(), NOW()),
    (2, 'iPhone 14', 2, 4, 'ไอโฟนรุ่นใหม่ล่าสุด รองรับ 5G เร็วแรง ถ่ายภาพสวยทุกสภาพแสง', 29700, 6, 6.1, 256, 'Midnight', 8, NOW(), NOW()),
    (3, 'iPhone 13 Pro', 2, 3, 'ไอโฟนรุ่นโปร จอ ProMotion 120Hz กล้องระดับมืออาชีพ', 33000, 6, 6.1, 256, 'Sierra Blue', 3, NOW(), NOW()),
    (4, 'iPhone 13', 2, 4, 'Previous gen base model', 23100, 4, 6.1, 128, 'Pink', 10, NOW(), NOW()),
    (5, 'iPhone 12 Pro Max', 2, 3, '2020 flagship model', 29700, 6, 6.7, 256, 'Pacific Blue', 4, NOW(), NOW()),
    (6, 'iPhone 12', 2, 4, '2020 base model', 19800, 4, 6.1, 128, 'Purple', 6, NOW(), NOW()),
    (7, 'iPhone SE 2022', 2, 3, 'Budget-friendly model', 14190, 4, 4.7, 64, 'Starlight', 15, NOW(), NOW()),
    (8, 'iPhone 14 Plus', 2, 4, 'iPhone 14 Plus 128GB สี Starlight เครื่องศูนย์ไทย โมเดล TH แบต 100% มีกล่องครบ ประกันศูนย์ถึง พ.ย. 68 ส่งฟรี', 29700, 6, 6.7, 256, 'Blue', 7, NOW(), NOW()),
    (9, 'iPhone 13 mini', 2, 3, 'Compact previous gen', 19800, 4, 5.4, 128, 'Green', 5, NOW(), NOW()),
    (10, 'iPhone 12 mini', 2, 4, 'Compact 2020 model', 16500, 4, 5.4, 64, 'Red', 4, NOW(), NOW()),

    (16, 'Galaxy S23 Ultra', 1, 3, 'Samsung Galaxy S23 Ultra 512GB สีดำปีศาจ สภาพนางฟ้า 99% ไร้รอย แถมเคสแท้ แบตอึดสุดๆ รองรับปากกา S-Pen อุปกรณ์ครบกล่อง ประกันศูนย์เหลือ 6 เดือน ส่งฟรี', 39600, NULL, 6.8, 512, NULL, 6, NOW(), NOW()),
    (17, 'Galaxy S23+', 1, 4, 'Premium flagship model', 33000, 8, 6.6, 256, 'Cream', 8, NOW(), NOW()),
    (18, 'Galaxy Z Fold4', 1, 3, 'สมาร์ทโฟนพับได้สุดล้ำ จอใหญ่เท่าแท็บเล็ต ทำงานได้หลากหลาย', 59400, 12, 7.6, 256, 'Phantom Green', 3, NOW(), NOW()),
    (19, 'Galaxy Z Flip4', 1, 4, 'Compact foldable', 33000, 8, 6.7, 128, 'Bora Purple', 5, NOW(), NOW()),
    (20, 'Galaxy A53 5G', 1, 3, 'มือถือ 5G สเปคดี กล้องเทพ แบตอึด คุ้มค่าน่าใช้', 14850, 6, 6.5, 128, 'Awesome Blue', 12, NOW(), NOW()),
    (21, 'Galaxy A33 5G', 1, 4, 'Budget 5G phone', 11550, 6, 6.4, 128, 'Awesome White', 15, NOW(), NOW()),
    (22, 'Galaxy S22', 1, 3, 'เรือธงตัวท็อปจาก Samsung พร้อม S Pen ในตัว กล้อง 200MP ซูมไกลสุด 100x', 26400, 8, 6.1, 128, 'Pink Gold', 7, NOW(), NOW()),
    (23, 'Galaxy M53', 1, 4, 'Mid-range performance', 14850, 6, 6.7, 128, 'Green', 9, NOW(), NOW()),
    (24, 'Galaxy A73 5G', 1, 3, 'Premium mid-range', 16500, 8, 6.7, 256, 'Gray', 6, NOW(), NOW()),
    (25, 'Galaxy S21 FE', 1, 4, 'Fan Edition model', 19800, 6, 6.4, 128, 'Olive', 8, NOW(), NOW()),

    (31, '13 Pro', 3, 3, 'เรือธงสเปคแรงจาก Xiaomi กล้องไลก้า ชาร์จไว 120W', 33000, 12, 6.73, 256, 'Black', 8, NOW(), NOW()),
    (32, '13T Pro', 3, 4, 'Xiaomi 13T Pro 12/512GB สี Meadow Green ชิป Dimensity 9200+ เร็วแรง กล้อง Leica ถ่ายรูปสวยขั้นเทพ มีที่ชาร์จ 120W ครบกล่อง จัดส่งฟรีทั่วประเทศ', 23100, 12, NULL, NULL, 'Alpine Blue', 6, NOW(), NOW()),
    (33, 'POCO F5', 3, 3, 'มือถือสเปคเทพ เน้นเล่นเกม จอ 120Hz ราคาคุ้มค่า', 13200, 8, 6.67, 256, 'Carbon Black', 10, NOW(), NOW()),
    (34, 'Redmi Note 12 Pro', 3, 4, 'กล้องคมชัด 108MP แบตอึด ชาร์จเร็ว ราคาโดนใจ', 9900, 8, 6.67, 128, 'Sky Blue', 15, NOW(), NOW()),
    (35, '12T Pro', 3, 3, 'Previous flagship', 21450, 8, 6.67, 256, 'Cosmic Black', 5, NOW(), NOW()),
    (36, 'POCO X5 Pro', 3, 4, 'Mid-range performer', 9900, 8, 6.67, 128, 'Yellow', 12, NOW(), NOW()),
    (37, 'Redmi 12C', 3, 3, 'Budget friendly', 5940, 4, 6.71, 64, 'Ocean Blue', 20, NOW(), NOW()),
    (38, '12 Lite', 3, 4, 'Slim mid-range', 13200, 8, 6.55, 128, 'Lite Pink', 8, NOW(), NOW()),
    (39, 'POCO M5', 3, 3, 'Budget gaming', 7590, 6, 6.58, 128, 'Power Black', 14, NOW(), NOW()),
    (40, 'Redmi Note 11', 3, 4, 'Previous gen mid-range', 8250, 6, 6.43, 128, 'Star Blue', 10, NOW(), NOW()),

    (46, 'P60 Pro', 4, 3, 'กล้องเรือธงระดับเทพ เซ็นเซอร์ใหญ่พิเศษ ถ่ายภาพกลางคืนสวยเยี่ยม', 36300, 12, 6.67, 256, 'Rococo Pearl', 5, NOW(), NOW()),
    (47, 'Mate 50 Pro', 4, 4, 'เรือธงตระกูล Mate จอ OLED คมชัด ดีไซน์พรีเมียม', 42900, 8, 6.74, 256, 'Silver Black', 4, NOW(), NOW()),
    (48, 'nova 11 Pro', 4, 3, 'สมาร์ทโฟนดีไซน์สวย กล้องหน้าคู่ เน้นเซลฟี่ ชาร์จไว', 19800, 8, 6.78, 256, 'Green', 8, NOW(), NOW()),
    (49, 'P50 Pro', 4, 4, 'Previous flagship', 29700, 8, 6.6, 256, 'Cocoa Gold', 6, NOW(), NOW()),
    (50, 'nova 10', 4, 3, 'Stylish mid-range', 16500, 8, 6.67, 128, 'Starry Silver', 10, NOW(), NOW()),
    (51, 'Mate X3', 4, 4, 'Premium foldable', 66000, 12, 7.85, 512, 'Feather Gold', 3, NOW(), NOW()),
    (52, 'nova 9', 4, 3, 'Previous mid-range', 13200, 8, 6.57, 128, 'Starry Blue', 12, NOW(), NOW()),
    (53, 'P50 Pocket', 4, 4, 'Foldable fashion', 46200, 8, 6.9, 256, 'Premium Gold', 4, NOW(), NOW()),
    (54, 'nova Y70', 4, 3, 'Budget friendly', 9900, 4, 6.75, 128, 'Crystal Blue', 15, NOW(), NOW()),
    (55, 'Mate 40 Pro', 4, 4, 'Classic flagship', 26400, 8, 6.76, 256, 'Mystic Silver', 5, NOW(), NOW()),

    (61, 'ROG Phone 7', 12, 3, 'สมาร์ทโฟนเกมมิ่งสเปคโหด จอ 165Hz เสียงสเตอริโอคู่ แบตอึด', 33000, 16, 6.78, 512, 'Phantom Black', 4, NOW(), NOW()),
    (62, 'ROG Phone 6D', 12, 4, 'เกมมิ่งโฟนพลังแรง CPU Dimensity ระบายความร้อนเยี่ยม', 29700, 16, 6.78, 256, 'Space Gray', 5, NOW(), NOW()),
    (63, 'Zenfone 9', 12, 3, 'มือถือกะทัดรัด สเปคแรง กล้องกันสั่น ใช้ง่ายมือเดียว', 23100, 8, 5.9, 128, 'Midnight Black', 8, NOW(), NOW()),
    (64, 'ROG Phone 6', 12, 4, 'Previous gaming flagship', 29700, 12, 6.78, 256, 'Storm White', 6, NOW(), NOW()),
    (65, 'Zenfone 8', 12, 3, 'Previous compact flagship', 19800, 8, 5.9, 128, 'Obsidian Black', 7, NOW(), NOW()),
    (66, 'ROG Phone 5s', 12, 4, 'Gaming performance', 26400, 12, 6.78, 256, 'Phantom Black', 5, NOW(), NOW()),
    (67, 'Zenfone 8 Flip', 12, 3, 'Flip camera flagship', 26400, 8, 6.67, 256, 'Galactic Black', 4, NOW(), NOW()),
    (68, 'ROG Phone 5', 12, 4, 'Classic gaming phone', 23100, 12, 6.78, 256, 'Storm White', 6, NOW(), NOW()),
    (69, 'Zenfone 7', 12, 3, 'Flip camera classic', 19800, 8, 6.67, 128, 'Aurora Black', 5, NOW(), NOW()),
    (70, 'ROG Phone 3', 12, 4, 'Legacy gaming phone', 16500, 12, 6.59, 256, 'Black Glare', 3, NOW(), NOW()),

    (76, 'Find X6 Pro', 10, 3, 'กล้องเทพระดับมืออาชีพ ชิป Snapdragon 8 Gen 2 ชาร์จไว 100W', 33000, 12, 6.82, 256, 'Cosmos Black', 5, NOW(), NOW()),
    (77, 'Reno9 Pro+', 10, 4, 'OPPO Reno9 Pro+ 5G 256GB สี Glossy Purple สวยสะดุดตา ใช้งานลื่นสุดๆ แบต 4700 mAh รองรับชาร์จไว ครบกล่อง + ใบเสร็จศูนย์ ส่งฟรี Flash Express', 23100, 12, 6.7, 256, 'Eternal Gold', 8, NOW(), NOW()),
    (78, 'Find N2 Flip', 10, 3, 'สมาร์ทโฟนพับได้สุดหรู จอนอกใหญ่พิเศษ กล้องคู่คมชัด', 33000, 8, 6.8, 256, 'Astral Black', 4, NOW(), NOW()),
    (79, 'Reno8 Pro', 10, 4, 'ดีไซน์บางเบา กล้องคมชัด ชาร์จเร็วสุด ระบบเสียงดี', 19800, 8, 6.7, 256, 'Glazed Green', 10, NOW(), NOW()),
    (80, 'Find X5 Pro', 10, 3, 'Previous flagship', 29700, 12, 6.7, 256, 'Ceramic White', 6, NOW(), NOW()),
    (81, 'A78', 10, 4, 'Mid-range performer', 9900, 8, 6.56, 128, 'Glowing Black', 15, NOW(), NOW()),
    (82, 'Reno7', 10, 3, 'Style focused mid-range', 13200, 8, 6.43, 128, 'Startrails Blue', 12, NOW(), NOW()),
    (83, 'Find X5 Lite', 10, 4, 'Previous gen lite', 14850, 8, 6.43, 128, 'Starry Black', 8, NOW(), NOW()),
    (84, 'A77', 10, 3, 'Budget friendly', 8250, 6, 6.56, 128, 'Ocean Blue', 20, NOW(), NOW()),
    (85, 'Reno6 Pro', 10, 4, 'Classic premium', 16500, 12, 6.55, 256, 'Arctic Blue', 7, NOW(), NOW());


CREATE TABLE attachments (
                             id INT AUTO_INCREMENT PRIMARY KEY,
                             saleItem_id INT NOT NULL,
                             filename VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                             file_path VARCHAR(255) NOT NULL,
                             file_size INT NOT NULL, -- ขนาดไฟล์เป็นไบต์
                             file_type ENUM('jpg', 'jpeg', 'png') NOT NULL,
                             image_view_order INT NOT NUll,
                             created_on DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
                             updated_on DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
                             CONSTRAINT fk_attachments_task
                                 FOREIGN KEY (saleItem_id)
                                     REFERENCES sale_items (id)
                                     ON DELETE CASCADE
                                     ON UPDATE CASCADE,
                             UNIQUE (saleItem_id, filename) -- ต้องการความ unique ของชื่อไฟล์ในแต่ละ task
) ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;