package org.csu.mybookstore.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.csu.mybookstore.dto.CategoryNavigation;
import org.csu.mybookstore.entity.Category;
import org.csu.mybookstore.mapper.CategoryMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品分类服务：提供首页分类导航的只读查询，不涉及商品查询、HTTP 与页面逻辑。
 */
@Service
public class CategoryService {

    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    /**
     * 查询首页分类导航。
     * 一次 selectList 读取全部分类，SQL 按 sort_order、id 升序，父子两级共用同一份结果；
     * 随后在内存中把二级分类挂到对应的一级分类下，不为每个一级分类单独查一次子分类。
     * 一级分类没有二级分类时 children 为空列表，数据访问异常继续向上抛出。
     */
    public List<CategoryNavigation> findNavigation() {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Category::getId, Category::getCode, Category::getParentId, Category::getName)
                .orderByAsc(Category::getSortOrder)
                .orderByAsc(Category::getId);
        List<Category> categories = categoryMapper.selectList(wrapper);

        // 排序已在 SQL 中完成，groupingBy 的收集列表保持遍历顺序，因此组内二级分类同样是 sort_order、id 升序
        Map<Long, List<Category>> childrenByParentId = categories.stream()
                .filter(category -> category.getParentId() != null)
                .collect(Collectors.groupingBy(Category::getParentId));

        return categories.stream()
                .filter(category -> category.getParentId() == null)
                .map(category -> new CategoryNavigation(
                        category.getId(),
                        category.getCode(),
                        category.getName(),
                        childrenByParentId.getOrDefault(category.getId(), List.of()).stream()
                                .map(CategoryService::toNavigationItem)
                                .toList()))
                .toList();
    }

    private static CategoryNavigation.Item toNavigationItem(Category category) {
        return new CategoryNavigation.Item(category.getId(), category.getCode(), category.getName());
    }

}