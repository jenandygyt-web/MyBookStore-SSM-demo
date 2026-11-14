package org.csu.mybookstore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 教学账号，对应 MyPetStore 数据库中的 store_user 表。
 */
@Getter
@Setter
@TableName("store_user")
public class User {

    /** 内部主键，数据库自增 BIGINT */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 登录名 */
    private String username;

    /** BCrypt 散列，不保存明文密码 */
    private String passwordHash;

    /** 显示名称 */
    private String displayName;

    /** 角色：CUSTOMER 或 ADMIN */
    private String role;

    /** 状态：ENABLED 或 DISABLED */
    private String status;

    /** 邮箱 */
    private String email;

    /** 手机号 */
    private String phone;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;

}
