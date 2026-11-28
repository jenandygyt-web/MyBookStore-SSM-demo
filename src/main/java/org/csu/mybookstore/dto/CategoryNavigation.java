package org.csu.mybookstore.dto;

import java.util.List;

/**
 * 首页分类导航条目：一个一级分类及其下属的二级分类。
 * 由 CategoryService 一次读取全部分类后在内存中组装，不新增数据库表。
 */
public record CategoryNavigation(
        Long id,
        String code,
        String name,
        List<Item> children
) {

    /**
     * 二级分类导航项：只包含展示与跳转需要的字段，不含父子关系与时间字段。
     */
    public record Item(
            Long id,
            String code,
            String name
    ) {
    }
}