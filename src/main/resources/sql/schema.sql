-- 在 MySQL 中执行本脚本，初始化 MyBookStore 演示库
-- 命令行：mysql -u root -p < schema.sql

CREATE DATABASE IF NOT EXISTS MyBookStore DEFAULT CHARACTER SET utf8mb4;

USE MyBookStore;

# DROP TABLE IF EXISTS `user`;
# CREATE TABLE user(
#     id BIGINT NOT NULL  AUTO_IN
# )

DROP TABLE IF EXISTS product;
CREATE TABLE product
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '图书编号',
    name        VARCHAR(100) NOT NULL COMMENT '书名',
    author      VARCHAR(50)  NOT NULL COMMENT '作者',
    price       DECIMAL(10, 2) COMMENT '价格',
    stock       INT          NOT NULL DEFAULT 0 COMMENT '库存数量',
    description TEXT COMMENT '图书简介',
    create_time DATETIME              DEFAULT CURRENT_TIMESTAMP COMMENT '登记时间'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT '商品列表';

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