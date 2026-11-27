package org.csu.mybookstore.mapper;

import org.csu.mybookstore.entity.Category;
import org.csu.mybookstore.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * CategoryMapper / ProductMapper 连接真实 MySQL 教学库的集成测试。
 * 数据来源为 02-schema-with-demo.sql 固定教学数据，全程只读：
 * 只调用 BaseMapper.selectById，不写库、不执行初始化脚本、不经 JdbcTemplate 二次查库。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("dev")
class CategoryProductMapperIntegrationTest {

    /** 二级分类 DOGS_PETS，父分类为一级分类 DOGS（犬类） */
    private static final Long SECOND_LEVEL_CATEGORY_ID = 101L;

    /** 商品 K9-BGL-03 比格犬，归属二级分类 DOGS_PETS */
    private static final Long PRODUCT_ID = 1L;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ProductMapper productMapper;

    @Test
    @DisplayName("selectById 读取二级分类时应正确映射父分类、排序与时间字段")
    void selectById_secondLevelCategory_mapsParentAndTimestamps() {
        Category category = categoryMapper.selectById(SECOND_LEVEL_CATEGORY_ID);

        assertThat(category).isNotNull();
        assertThat(category.getId()).isEqualTo(SECOND_LEVEL_CATEGORY_ID);
        assertThat(category.getCode()).isEqualTo("DOGS_PETS");
        assertThat(category.getName()).isEqualTo("宠物");
        assertThat(category.getSortOrder()).isEqualTo(10);
        // 二级分类的 parentId 指向一级分类；生成列 parent_key 为 COALESCE(parent_id, 0)
        assertThat(category.getParentId()).isEqualTo(1L);
        assertThat(category.getParentKey()).isEqualTo(1L);
        // DATETIME 映射为 LocalDateTime
        assertThat(category.getCreatedAt()).isNotNull();
        assertThat(category.getUpdatedAt()).isNotNull();
        assertThat(category.getUpdatedAt()).isAfterOrEqualTo(category.getCreatedAt());

        // 用父主键反查一级分类，确认父子关系映射一致
        Category parent = categoryMapper.selectById(category.getParentId());
        assertThat(parent).isNotNull();
        assertThat(parent.getParentId()).isNull();
        assertThat(parent.getCode()).isEqualTo("DOGS");
        assertThat(parent.getName()).isEqualTo("犬类");
    }

    @Test
    @DisplayName("selectById 读取商品时应正确映射 BigDecimal 价格、库存、状态与时间字段")
    void selectById_product_mapsPriceStockStatusAndTimestamps() {
        Product product = productMapper.selectById(PRODUCT_ID);

        assertThat(product).isNotNull();
        assertThat(product.getId()).isEqualTo(PRODUCT_ID);
        assertThat(product.getSku()).isEqualTo("K9-BGL-03");
        assertThat(product.getName()).isEqualTo("比格犬");
        assertThat(product.getCategoryId()).isEqualTo(SECOND_LEVEL_CATEGORY_ID);
        assertThat(product.getCoverImage()).isEqualTo("images/products/K9-BGL-03/1.jpg");
        // DECIMAL(12,2) 应保留两位小数精度，而不是 double 近似值
        assertThat(product.getPrice()).isEqualByComparingTo(new BigDecimal("1280.00"));
        assertThat(product.getPrice().scale()).isEqualTo(2);
        assertThat(product.getStock()).isEqualTo(10);
        assertThat(product.getStatus()).isEqualTo("ON_SALE");
        assertThat(product.getSortOrder()).isEqualTo(10);
        assertThat(product.getCreatedAt()).isNotNull();
        assertThat(product.getUpdatedAt()).isNotNull();
        assertThat(product.getUpdatedAt()).isAfterOrEqualTo(product.getCreatedAt());

        // 商品归属的分类必须是二级分类，且能定位到其父分类
        Category category = categoryMapper.selectById(product.getCategoryId());
        assertThat(category).isNotNull();
        assertThat(category.getParentId()).isNotNull();
    }
}