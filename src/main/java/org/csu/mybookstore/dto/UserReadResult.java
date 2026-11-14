package org.csu.mybookstore.dto;

/**
 * 用户展示结果：只包含可对外展示的安全字段，不含密码散列。
 */
public record UserReadResult(
        Long id,
        String username,
        String displayName,
        String role,
        String status
) {
}
