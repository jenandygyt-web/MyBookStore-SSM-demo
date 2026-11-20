-- MyBookStore.`user` 定义
create database if not exists MyBookStore character set utf8mb4 collate utf8mb4_0900_ai_ci;
use MyBookStore;
CREATE TABLE `user` (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `username` varchar(100) DEFAULT NULL,
                        `password` varchar(100) NOT NULL,
                        `status` tinyint(1) NOT NULL DEFAULT '1',
                        `is_admin` tinyint(1) NOT NULL DEFAULT '0',
                        `phone` varchar(100) DEFAULT NULL,
                        `email` varchar(100) DEFAULT NULL,
                        `create_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        `update_at` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP,
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `user_unique` (`username`),
                        CONSTRAINT `user_check` CHECK ((char_length(trim(`password`)) > 0)),
                        CONSTRAINT `user_check_1` CHECK ((`status` in (1,0))),
                        CONSTRAINT `user_check_2` CHECK ((`is_admin` in (0,1))),
                        CONSTRAINT `user_check_3` CHECK ((char_length(trim(`username`)) > 0))
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
                            UNIQUE KEY `category_unique_1` (`parent_key`,`name`),
                            KEY `category_parent_key_IDX` (`parent_key`,`sort_order`,`id`) USING BTREE,
                            KEY `category_category_FK` (`parent_id`),
                            CONSTRAINT `category_category_FK` FOREIGN KEY (`parent_id`) REFERENCES `category` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                            CONSTRAINT `category_check` CHECK ((char_length(trim(`code`)) > 0)),
                            CONSTRAINT `category_check_1` CHECK ((char_length(trim(`name`)) > 0)),
                            CONSTRAINT `category_check_2` CHECK ((`sort_order` >= 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


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
                         `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         `update_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         `payment_method` varchar(16) DEFAULT NULL,
                         `payment_no` varchar(40) DEFAULT NULL,
                         `paid_at` datetime DEFAULT NULL,
                         `cancelled_at` datetime DEFAULT NULL,
                         `shipped_at` datetime DEFAULT NULL,
                         `completed_at` datetime DEFAULT NULL,
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `order_unique` (`order_no`),
                         UNIQUE KEY `order_unique_1` (`payment_no`),
                         KEY `order_user_id_IDX` (`user_id`,`created_at`,`id`) USING BTREE,
                         KEY `order_status_IDX` (`status`,`created_at`,`id`) USING BTREE,
                         CONSTRAINT `order_user_FK` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                         CONSTRAINT `order_check` CHECK ((char_length(trim(`order_no`)) > 0)),
                         CONSTRAINT `order_check_1` CHECK ((`total_amount` > 0)),
                         CONSTRAINT `order_check_2` CHECK (((char_length(trim(`receiver_name`)) > 0) and (char_length(trim(`receiver_phone`)) > 0) and (char_length(trim(`receiver_address`)) > 0))),
                         CONSTRAINT `order_check_3` CHECK ((`status` in (_utf8mb4'PENDING_PAYMENT',_utf8mb4'PAID',_utf8mb4'SHIPPED',_utf8mb4'COMPELTED',_utf8mb4'CANCELLED'))),
                         CONSTRAINT `order_check_4` CHECK ((((`status` in (_utf8mb4'PENDING_PAYMENT',_utf8mb4'CANCELLED')) and (`payment_method` is null) and (`payment_no` is null) and (`paid_at` is null)) or ((`status` in (_utf8mb4'PAID',_utf8mb4'SHIPPED',_utf8mb4'COMPLETED')) and (`payment_method` is not null) and (`payment_method` = _utf8mb4'MOCK') and (`payment_no` is not null) and (char_length(trim(`payment_no`)) > 0) and (`paid_at` is not null)))),
                         CONSTRAINT `order_check_5` CHECK ((((`status` = _utf8mb4'CANCELLED') and (`cancelled_at` is not null)) or ((`status` <> _utf8mb4'CANCELLED') and (`cancelled_at` is null)))),
                         CONSTRAINT `order_check_6` CHECK ((((`status` in (_utf8mb4'SHIPPED',_utf8mb4'COMPLETED')) and (`shipped_at` is not null)) or ((`status` not in (_utf8mb4'SHIPPED',_utf8mb4'COMPLETED')) and (`shipped_at` is null)))),
                         CONSTRAINT `order_check_7` CHECK ((((`status` = _utf8mb4'COMPLETED') and (`completed_at` is not null)) or ((`status` <> _utf8mb4'COMPLETED') and (`completed_at` is null)))),
                         CONSTRAINT `order_check_8` CHECK ((((`paid_at` is null) or (`paid_at` >= `created_at`)) and ((`cancelled_at` is null) or (`cancelled_at` >= `created_at`)) and ((`shipped_at` is null) or (`shipped_at` >= `paid_at`)) and ((`completed_at` is null) or (`completed_at` >= `shipped_at`))))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- MyBookStore.product 定义

CREATE TABLE `product` (
                           `id` bigint NOT NULL AUTO_INCREMENT COMMENT '图书编号',
                           `sku` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                           `category_id` bigint NOT NULL,
                           `name` varchar(100) NOT NULL COMMENT '书名',
                           `author` varchar(50) NOT NULL COMMENT '作者',
                           `cover_image` varchar(255) NOT NULL DEFAULT 'images/logo.png',
                           `price` decimal(10,2) DEFAULT NULL COMMENT '价格',
                           `stock` int NOT NULL DEFAULT '0' COMMENT '库存数量',
                           `description` text COMMENT '图书简介',
                           `on_sale` tinyint(1) NOT NULL DEFAULT '1',
                           `sort_order` int NOT NULL DEFAULT '0',
                           `version` bigint NOT NULL DEFAULT '0',
                           `create_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登记时间',
                           `update_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `product_unique` (`sku`),
                           KEY `product_category_id_IDX` (`category_id`,`on_sale`,`sort_order`,`id`) USING BTREE,
                           KEY `product_on_sale_IDX` (`on_sale`,`sort_order`,`id`) USING BTREE,
                           CONSTRAINT `product_category_FK` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                           CONSTRAINT `product_check` CHECK ((char_length(trim(`sku`)) > 0)),
                           CONSTRAINT `product_check_1` CHECK ((char_length(trim(`name`)) > 0)),
                           CONSTRAINT `product_check_2` CHECK ((char_length(trim(`cover_image`)) > 0)),
                           CONSTRAINT `product_check_3` CHECK ((`price` > 0)),
                           CONSTRAINT `product_check_4` CHECK ((`stock` >= 0)),
                           CONSTRAINT `product_check_5` CHECK ((`on_sale` in (0,1))),
                           CONSTRAINT `product_check_6` CHECK ((`sort_order` >= 0)),
                           CONSTRAINT `product_check_7` CHECK ((`version` >= 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品列表';


-- MyBookStore.product_image 定义

CREATE TABLE `product_image` (
                                 `id` bigint NOT NULL AUTO_INCREMENT,
                                 `product_id` bigint NOT NULL,
                                 `image_path` varchar(255) NOT NULL,
                                 `alt_text` varchar(120) DEFAULT NULL,
                                 `sort_order` int NOT NULL DEFAULT '0',
                                 PRIMARY KEY (`id`),
                                 KEY `product_image_product_id_IDX` (`product_id`,`sort_order`,`id`) USING BTREE,
                                 CONSTRAINT `product_image_product_FK` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                 CONSTRAINT `product_image_check` CHECK ((char_length(trim(`image_path`)) > 0)),
                                 CONSTRAINT `product_image_check_1` CHECK ((`sort_order` >= 0))
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
                             PRIMARY KEY (`id`),
                             UNIQUE KEY `cart_item_unique` (`user_id`,`product_id`),
                             KEY `cart_item_product_FK` (`product_id`),
                             CONSTRAINT `cart_item_product_FK` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                             CONSTRAINT `cart_item_user_FK` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                             CONSTRAINT `cart_item_check` CHECK ((`quantity` between 1 and 99)),
                             CONSTRAINT `cart_item_check_1` CHECK ((`version` >= 0))
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
                              UNIQUE KEY `order_item_unique` (`order_id`,`product_id`),
                              KEY `idx_order_item_product` (`product_id`),
                              CONSTRAINT `fk_order_item_order` FOREIGN KEY (`order_id`) REFERENCES `order` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                              CONSTRAINT `fk_order_item_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                              CONSTRAINT `ck_order_item_amount` CHECK (((`line_amount` > 0) and (`line_amount` = (`unit_price` * `quantity`)))),
                              CONSTRAINT `ck_order_item_cover` CHECK ((char_length(trim(`cover_image_snapshot`)) > 0)),
                              CONSTRAINT `ck_order_item_name` CHECK ((char_length(trim(`product_name_snapshot`)) > 0)),
                              CONSTRAINT `ck_order_item_price` CHECK ((`unit_price` > 0)),
                              CONSTRAINT `ck_order_item_quantity` CHECK ((`quantity` between 1 and 99)),
                              CONSTRAINT `ck_order_item_sku` CHECK ((char_length(trim(`sku_snapshot`)) > 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;