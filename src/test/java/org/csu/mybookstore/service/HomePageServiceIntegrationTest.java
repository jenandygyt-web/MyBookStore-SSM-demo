package org.csu.mybookstore.service;

import org.csu.mybookstore.dto.CategoryNavigation;
import org.csu.mybookstore.entity.Category;
import org.csu.mybookstore.entity.Product;
import org.csu.mybookstore.mapper.CategoryMapper;
import org.csu.mybookstore.mapper.ProductMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 首页三个 Service 查询连接真实 MySQL 教学库的集成测试。
 * 全程只读：只调用 Mapper.selectList(null) 全量读取数据，不写库、不执行初始化脚本、不重置数据库。
 * 预期值不硬编码业务数据，而是在 Java 中用独立逻辑（显式 Comparator 排序、显式过滤、limit 截断）
 * 重新计算后与 Service 执行结果逐项核对，避免测试与实现共用同一套排序/过滤代码而失去校验意义。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("dev")
class HomePageServiceIntegrationTest {

    /** 与 ProductService 相同的状态约定：只有上架商品对外展示 */
    private static final String STATUS_ON_SALE = "ON_SALE";

    /** 新品栏目最多展示条数，与 ProductService 约定一致 */
    private static final int MAX_NEW_PRODUCTS = 3;

    /** 精选栏目最多展示条数，与 ProductService 约定一致 */
    private static final int MAX_FEATURED_PRODUCTS = 4;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ProductMapper productMapper;

    @Test
    @DisplayName("分类导航：只含一级分类，一级与二级分组正确且均按 sort_order、id 升序")
    void findNavigation_matchesIndependentlyComputedGroupingAndOrder() {
        List<CategoryNavigation> actual = categoryService.findNavigation();

        // 独立计算预期：全量读取分类，在 Java 中排序，一级按 parentId 为空筛选，二级按父 id 归属
        List<Category> sorted = categoryMapper.selectList(null).stream()
                .sorted(Comparator.comparing(Category::getSortOrder).thenComparing(Category::getId))
                .toList();
        List<Category> expectedRoots = sorted.stream()
                .filter(category -> category.getParentId() == null)
                .toList();

        assertThat(actual).hasSize(expectedRoots.size());
        for (int i = 0; i < expectedRoots.size(); i++) {
            Category expectedRoot = expectedRoots.get(i);
            CategoryNavigation navigation = actual.get(i);
            assertThat(navigation.id()).isEqualTo(expectedRoot.getId());
            assertThat(navigation.code()).isEqualTo(expectedRoot.getCode());
            assertThat(navigation.name()).isEqualTo(expectedRoot.getName());

            // sorted 已按 sort_order、id 升序，filter 保持遍历顺序，组内二级分类顺序可直接核对
            List<Category> expectedChildren = sorted.stream()
                    .filter(category -> expectedRoot.getId().equals(category.getParentId()))
                    .toList();
            assertThat(navigation.children())
                    .extracting(CategoryNavigation.Item::id)
                    .containsExactlyElementsOf(expectedChildren.stream().map(Category::getId).toList());
            assertThat(navigation.children())
                    .extracting(CategoryNavigation.Item::code)
                    .containsExactlyElementsOf(expectedChildren.stream().map(Category::getCode).toList());
            assertThat(navigation.children())
                    .extracting(CategoryNavigation.Item::name)
                    .containsExactlyElementsOf(expectedChildren.stream().map(Category::getName).toList());
        }
    }

    @Test
    @DisplayName("新品：只含 ON_SALE，按 created_at、id 倒序且最多 3 条")
    void getNewProducts_matchesIndependentlyComputedFilterOrderAndLimit() {
        List<Product> actual = productService.getNewProducts();

        List<Product> onSale = onSaleProducts();
        // 数据前提：上架商品足够多，保证 LIMIT 3 的截断逻辑被真实触发
        assertThat(onSale).hasSizeGreaterThanOrEqualTo(MAX_NEW_PRODUCTS);

        // 独立计算：created_at 倒序（NULL 视为最小、排最后，与 MySQL DESC 一致），同时间按 id 倒序兜底
        List<Long> expectedIds = onSale.stream()
                .sorted(Comparator
                        .comparing(Product::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(Product::getId, Comparator.reverseOrder()))
                .limit(MAX_NEW_PRODUCTS)
                .map(Product::getId)
                .toList();

        assertThat(actual).hasSize(MAX_NEW_PRODUCTS);
        assertThat(actual).extracting(Product::getId).containsExactlyElementsOf(expectedIds);
        assertThat(actual).allMatch(product -> STATUS_ON_SALE.equals(product.getStatus()));
    }

    @Test
    @DisplayName("精选：只含 ON_SALE，按 sort_order、id 升序且最多 4 条")
    void getFeaturedProducts_matchesIndependentlyComputedFilterOrderAndLimit() {
        List<Product> actual = productService.getFeaturedProducts();

        List<Product> onSale = onSaleProducts();
        // 数据前提：上架商品足够多，保证 LIMIT 4 的截断逻辑被真实触发
        assertThat(onSale).hasSizeGreaterThanOrEqualTo(MAX_FEATURED_PRODUCTS);

        // 独立计算：sort_order 升序（NULL 视为最小、排最前，与 MySQL ASC 一致），同序按 id 升序兜底
        List<Long> expectedIds = onSale.stream()
                .sorted(Comparator
                        .comparing(Product::getSortOrder, Comparator.nullsFirst(Comparator.naturalOrder()))
                        .thenComparing(Product::getId))
                .limit(MAX_FEATURED_PRODUCTS)
                .map(Product::getId)
                .toList();

        assertThat(actual).hasSize(MAX_FEATURED_PRODUCTS);
        assertThat(actual).extracting(Product::getId).containsExactlyElementsOf(expectedIds);
        assertThat(actual).allMatch(product -> STATUS_ON_SALE.equals(product.getStatus()));
    }

    /** 全量读取商品后只保留 ON_SALE，直接核对真实库数据，不模拟业务数据 */
    private List<Product> onSaleProducts() {
        return productMapper.selectList(null).stream()
                .filter(product -> STATUS_ON_SALE.equals(product.getStatus()))
                .toList();
    }
}
