package org.csu.mybookstore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 图书，对应 mybookstore 数据库中的 book 表。
 */
@Data
@TableName("book")
public class Book {

    /** 主键，数据库自增 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 书名 */
    private String name;

    /** 作者 */
    private String author;

    /** 价格 */
    private BigDecimal price;

    /** 库存数量 */
    private Integer stock;

    /** 图书简介 */
    private String description;

    /** 登记时间 */
    private LocalDateTime createTime;

}
