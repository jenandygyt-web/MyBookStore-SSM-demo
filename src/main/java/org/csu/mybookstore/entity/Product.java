package org.csu.mybookstore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品，对应 mybookstore 数据库中的 product 表。
 * 一个商品对应一个可销售 SKU，不做多规格；下架通过 status 表示，不物理删除。
 */
@Getter
@Setter
@TableName("product")
public class Product {

    /** 内部主键，数据库自增 BIGINT */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 唯一销售编码，创建后不修改 */
    private String sku;

    /** 所属分类主键，必须归属二级分类（由 Service 校验） */
    private Long categoryId;

    /** 商品名称 */
    private String name;

    /** 简介，可为空 */
    private String summary;

    /** 纯文本详情，模板中转义展示 */
    private String description;

    /** 封面图站内静态资源相对路径 */
    private String coverImage;

    /** 售价，对应 DECIMAL(12,2)，Java 使用 BigDecimal */
    private BigDecimal price;

    /** 可销售库存：下单扣减，未支付取消恢复 */
    private Integer stock;

    /** 状态：DRAFT 或 ON_SALE */
    private String status;

    /** 排序值，越小越靠前 */
    private Integer sortOrder;

    /** 版本号，所有商品写操作显式递增，防旧值覆盖 */
    private Long version;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;

}