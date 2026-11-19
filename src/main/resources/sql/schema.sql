-- MyBookStore.`order` 定义

CREATE TABLE `order` (
                         `id` bigint NOT NULL AUTO_INCREMENT,
                         `order_no` varchar(40) NOT NULL,
                         `user_id` bigint NOT NULL,
                         `status` varchar(16) NOT NULL,
                         `total_amount` decimal(12,2) NOT NULL,
                         `receiver_name` varchar(50) NOT NULL,
                         `receiver_phone` varchar(50) NOT NULL,
                         `receiver_address` varchar(255) NOT NULL,
                         `create_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         `update_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         `payment_method` varchar(16) DEFAULT NULL,
                         `payment_no` varchar(40) DEFAULT NULL,
                         `paid_at` datetime DEFAULT NULL,
                         `cancelled_at` datetime DEFAULT NULL,
                         `shipped_at` datetime DEFAULT NULL,
                         `completed_at` datetime DEFAULT NULL,
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `order_unique` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- MyBookStore.order_item 定义

CREATE TABLE `order_item` (
                              `id` bigint NOT NULL AUTO_INCREMENT,
                              `order_id` bigint NOT NULL,
                              `product_id` bigint NOT NULL,
                              `sku_snapshot` varchar(40) NOT NULL,
                              `product_name_snapshot` varchar(120) NOT NULL,
                              `cover_image_snapshot` varchar(255) NOT NULL,
                              `unit_price` decimal(12,2) NOT NULL,
                              `quantity` int NOT NULL,
                              `line_amount` decimal(12,2) NOT NULL,
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `order_item_unique` (`order_id`,`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- MyBookStore.product 定义

CREATE TABLE `product` (
                           `id` bigint NOT NULL AUTO_INCREMENT COMMENT '图书编号',
                           `name` varchar(100) NOT NULL COMMENT '书名',
                           `author` varchar(50) NOT NULL COMMENT '作者',
                           `price` decimal(10,2) DEFAULT NULL COMMENT '价格',
                           `stock` int NOT NULL DEFAULT '0' COMMENT '库存数量',
                           `description` text COMMENT '图书简介',
                           `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '登记时间',
                           PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品列表';


-- MyBookStore.`user` 定义

CREATE TABLE `user` (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `username` varchar(100) DEFAULT NULL,
                        `password` varchar(100) NOT NULL,
                        `status` tinyint(1) NOT NULL DEFAULT '1',
                        `is_admin` varchar(100) NOT NULL DEFAULT '0',
                        `phone` varchar(100) DEFAULT NULL,
                        `email` varchar(100) DEFAULT NULL,
                        `create_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        `update_at` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP,
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `user_unique` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- MyBookStore.cart_item 定义

CREATE TABLE `cart_item` (
                             `id` bigint NOT NULL AUTO_INCREMENT,
                             `user_id` bigint NOT NULL,
                             `product_id` bigint NOT NULL,
                             `quantity` int NOT NULL DEFAULT '1',
                             `version` bigint NOT NULL DEFAULT '0',
                             `create_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             `update_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- MyBookStore.product_image 定义

CREATE TABLE `product_image` (
                                 `id` bigint NOT NULL AUTO_INCREMENT,
                                 `product_id` bigint NOT NULL,
                                 `image_path` varchar(255) NOT NULL,
                                 `alt_text` varchar(120) DEFAULT NULL,
                                 `sort_order` int NOT NULL DEFAULT '0',
                                 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- MyBookStore.category 定义

CREATE TABLE `category` (
                            `id` bigint NOT NULL AUTO_INCREMENT,
                            `code` varchar(32) NOT NULL,
                            `parent_id` bigint DEFAULT NULL,
                            `parent_key` bigint DEFAULT NULL,
                            `name` varchar(50) NOT NULL,
                            `sort_order` int NOT NULL,
                            `create_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            `update_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `category_unique` (`code`),
                            UNIQUE KEY `category_unique_1` (`parent_key`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;