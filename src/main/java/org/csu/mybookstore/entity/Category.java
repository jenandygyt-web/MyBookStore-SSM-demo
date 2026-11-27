package org.csu.mybookstore.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 商品分类，对应 mybookstore 数据库中的 category 表。
 * 固定两级：parentId 为空表示一级分类，二级分类必须指向一级分类（由 Service 校验）。
 * 表内 parent_key 是数据库生成列，只读，不参与插入和更新。
 */
@Getter
@Setter
@TableName("category")
public class Category {

    /** 内部主键，数据库自增 BIGINT */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 全局唯一编码，创建后不修改 */
    private String code;

    /** 上级分类主键；一级分类为空，二级分类指向一级分类 */
    private Long parentId;

    /**
     * 数据库生成列 parent_key（COALESCE(parent_id, 0)），仅用于数据库唯一索引。
     * 插入和更新时由 MyBatis-Plus 忽略，列值始终由数据库维护。
     */
    @TableField(value = "parent_key", insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    private Long parentKey;

    /** 分类名称，同一上级下不可重名 */
    private String name;

    /** 排序值，越小越靠前 */
    private Integer sortOrder;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;

}