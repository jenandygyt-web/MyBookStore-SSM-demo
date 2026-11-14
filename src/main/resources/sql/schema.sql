-- 在 MySQL 中执行本脚本，初始化 MyBookStore 演示库
-- 命令行：mysql -u root -p < schema.sql

CREATE DATABASE IF NOT EXISTS mybookstore DEFAULT CHARACTER SET utf8mb4;

USE mybookstore;

DROP TABLE IF EXISTS book;
CREATE TABLE book
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '图书编号',
    name        VARCHAR(100) NOT NULL COMMENT '书名',
    author      VARCHAR(50)  NOT NULL COMMENT '作者',
    price       DECIMAL(10, 2) COMMENT '价格',
    stock       INT          NOT NULL DEFAULT 0 COMMENT '库存数量',
    description TEXT COMMENT '图书简介',
    create_time DATETIME              DEFAULT CURRENT_TIMESTAMP COMMENT '登记时间'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT '图书表';

INSERT INTO book (name, author, price, stock, description)
VALUES ('三体', '刘慈欣', 59.00, 12, '地球往事三部曲之一'),
       ('活着', '余华', 39.50, 8, '讲述一个人一生的苦难与坚韧'),
       ('围城', '钱钟书', 42.00, 5, '中国现代文学史上的讽刺经典');
