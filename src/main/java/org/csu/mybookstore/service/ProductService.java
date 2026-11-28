package org.csu.mybookstore.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.csu.mybookstore.entity.Product;
import org.csu.mybookstore.mapper.ProductMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品查询服务：提供前台展示所需的只读商品查询，不涉及 HTTP 与页面逻辑。
 */
@Service
public class ProductService {

    /** 只有上架商品对外展示 */
    private static final String STATUS_ON_SALE = "ON_SALE";

    /** 新品栏目最多展示条数 */
    private static final int MAX_NEW_PRODUCTS = 3;

    /** 精选栏目最多展示条数 */
    private static final int MAX_FEATURED_PRODUCTS = 4;

    private final ProductMapper productMapper;

    public ProductService(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    /**
     * 查询最近录入的上架商品，最多 3 条。
     * created_at 是商品录入时间，只用于“最近录入”排序，不代表本周销量或首次上架时间；
     * 过滤条件只有 status = ON_SALE，不校验库存，上架零库存商品仍可返回。
     * 按 created_at、id 倒序，id 兜底保证同一录入时间下顺序稳定。
     */
    public List<Product> getNewProducts() {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, STATUS_ON_SALE)
                .orderByDesc(Product::getCreatedAt)
                .orderByDesc(Product::getId)
                .last("LIMIT " + MAX_NEW_PRODUCTS);
        return productMapper.selectList(wrapper);
    }

    /**
     * 查询精选栏目展示的上架商品，最多 4 条。
     * 顺序完全由人工维护的 sort_order 决定，所有游客看到相同结果，id 兜底保证同序时稳定；
     * 不做个性化推荐、不统计销量、不读取登录态，因此与请求方无关。
     * 只按 status = ON_SALE 过滤，不校验库存；与新品栏目相互独立，允许返回相同商品。
     */
    public List<Product> getFeaturedProducts() {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, STATUS_ON_SALE)
                .orderByAsc(Product::getSortOrder)
                .orderByAsc(Product::getId)
                .last("LIMIT " + MAX_FEATURED_PRODUCTS);
        return productMapper.selectList(wrapper);
    }

}