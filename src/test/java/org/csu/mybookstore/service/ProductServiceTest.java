package org.csu.mybookstore.service;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.csu.mybookstore.entity.Product;
import org.csu.mybookstore.mapper.ProductMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * ProductService 纯单元测试：Mockito 模拟 ProductMapper，构造真实 ProductService，
 * 不启动 Spring / Web，也不连接数据库。
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductMapper productMapper;

    private ProductService productService;

    /**
     * Lambda 条件查询会即时解析方法引用 -> 列名，需要 Product 的 TableInfo 缓存。
     * 这里只为本测试类补齐必要初始化，不启动完整应用。
     */
    @BeforeAll
    static void initMybatisPlusMetadata() {
        MybatisConfiguration configuration = new MybatisConfiguration();
        GlobalConfig globalConfig = GlobalConfigUtils.defaults();
        GlobalConfigUtils.setGlobalConfig(configuration, globalConfig);
        MapperBuilderAssistant assistant = new MapperBuilderAssistant(configuration, "");
        TableInfoHelper.initTableInfo(assistant, Product.class);
    }

    @BeforeEach
    void setUp() {
        productService = new ProductService(productMapper);
    }

    @Test
    @DisplayName("只按 ON_SALE 过滤，按 created_at、id 倒序并限制 3 条，不校验库存")
    void getNewProducts_queriesOnSaleProductsOrderedByCreatedAtAndIdWithLimit() {
        when(productMapper.selectList(anyWrapper())).thenReturn(List.of());

        productService.getNewProducts();

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Wrapper<Product>> captor = ArgumentCaptor.forClass(Wrapper.class);
        verify(productMapper).selectList(captor.capture());
        Wrapper<Product> wrapper = captor.getValue();

        String sqlSegment = wrapper.getSqlSegment();
        // 过滤条件只有上架状态；零库存商品也要能展示，因此不能出现库存条件
        assertThat(sqlSegment).contains("status =").doesNotContain("stock");
        assertThat(((AbstractWrapper<?, ?, ?>) wrapper).getParamNameValuePairs()).containsValue("ON_SALE");
        // 先按录入时间倒序，同一时间再按 id 倒序，最后限制 3 条
        assertThat(sqlSegment).contains("created_at DESC").contains("id DESC").contains("LIMIT 3");
        assertThat(sqlSegment.indexOf("created_at DESC")).isLessThan(sqlSegment.indexOf("id DESC"));
    }

    @Test
    @DisplayName("上架零库存商品仍会返回，且保持 Mapper 返回顺序")
    void getNewProducts_includesOnSaleProductWithZeroStock() {
        LocalDateTime now = LocalDateTime.of(2026, 11, 28, 10, 0);
        when(productMapper.selectList(anyWrapper())).thenReturn(List.of(
                product(3L, "最新录入的书", new BigDecimal("39.00"), 0, now),
                product(2L, "第二新的书", new BigDecimal("49.00"), 8, now.minusDays(1)),
                product(1L, "第三新的书", new BigDecimal("59.00"), 5, now.minusDays(2))));

        List<Product> products = productService.getNewProducts();

        assertThat(products).hasSize(3);
        assertThat(products).extracting(Product::getId).containsExactly(3L, 2L, 1L);
        assertThat(products.get(0).getStock()).isZero();
    }

    @Test
    @DisplayName("没有上架商品时返回空列表")
    void getNewProducts_noOnSaleProduct_returnsEmptyList() {
        when(productMapper.selectList(anyWrapper())).thenReturn(List.of());

        assertThat(productService.getNewProducts()).isEmpty();
    }

    @Test
    @DisplayName("Mapper 抛出 DataAccessResourceFailureException 时应继续抛出同一个异常")
    void getNewProducts_whenMapperThrows_propagatesSameException() {
        DataAccessResourceFailureException failure =
                new DataAccessResourceFailureException("db unavailable");
        when(productMapper.selectList(anyWrapper())).thenThrow(failure);

        assertThatThrownBy(() -> productService.getNewProducts())
                .isInstanceOf(DataAccessResourceFailureException.class)
                .isSameAs(failure);
    }

    /**
     * 匹配任意条件包装器；用泛型方法代替 any(Wrapper.class) 的原始类型写法，避免未检查转换告警。
     */
    private static Wrapper<Product> anyWrapper() {
        return any();
    }

    private static Product product(Long id, String name, BigDecimal price, Integer stock, LocalDateTime createdAt) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPrice(price);
        product.setStock(stock);
        product.setCreatedAt(createdAt);
        return product;
    }
}