package org.csu.mybookstore.service;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.csu.mybookstore.dto.CategoryNavigation;
import org.csu.mybookstore.entity.Category;
import org.csu.mybookstore.mapper.CategoryMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * CategoryService 纯单元测试：Mockito 模拟 CategoryMapper，构造真实 CategoryService，
 * 不启动 Spring / Web，也不连接数据库。
 */
@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryMapper categoryMapper;

    private CategoryService categoryService;

    /**
     * Lambda 条件查询会即时解析方法引用 -> 列名，需要 Category 的 TableInfo 缓存。
     * 这里只为本测试类补齐必要初始化，不启动完整应用。
     */
    @BeforeAll
    static void initMybatisPlusMetadata() {
        MybatisConfiguration configuration = new MybatisConfiguration();
        GlobalConfig globalConfig = GlobalConfigUtils.defaults();
        GlobalConfigUtils.setGlobalConfig(configuration, globalConfig);
        MapperBuilderAssistant assistant = new MapperBuilderAssistant(configuration, "");
        TableInfoHelper.initTableInfo(assistant, Category.class);
    }

    @BeforeEach
    void setUp() {
        categoryService = new CategoryService(categoryMapper);
    }

    @Test
    @DisplayName("一次查询即可按 sort_order、id 升序组织一级分类及其二级分类")
    void findNavigation_groupsChildrenUnderParentAndKeepsSortOrder() {
        // 模拟 SQL 已按 sort_order、id 升序返回：一级分类 1、2 在前，二级分类按 sort_order 穿插在后
        when(categoryMapper.selectList(anyWrapper())).thenReturn(List.of(
                category(1L, "DOGS", null, "犬类", 1),
                category(2L, "CATS", null, "猫类", 2),
                category(101L, "DOGS_PETS", 1L, "宠物", 10),
                category(201L, "CATS_HOME", 2L, "家猫", 10),
                category(102L, "DOGS_WORK", 1L, "工作犬", 20)));

        List<CategoryNavigation> navigation = categoryService.findNavigation();

        assertThat(navigation).hasSize(2);
        assertThat(navigation).extracting(CategoryNavigation::code).containsExactly("DOGS", "CATS");

        CategoryNavigation dogs = navigation.get(0);
        assertThat(dogs.id()).isEqualTo(1L);
        assertThat(dogs.name()).isEqualTo("犬类");
        assertThat(dogs.children()).extracting(CategoryNavigation.Item::code)
                .containsExactly("DOGS_PETS", "DOGS_WORK");

        CategoryNavigation cats = navigation.get(1);
        assertThat(cats.children()).extracting(CategoryNavigation.Item::name)
                .containsExactly("家猫");

        // 需求要求只查一次：既没有按父分类循环查询，也没有额外读取其它表
        verify(categoryMapper, times(1)).selectList(anyWrapper());
        verifyNoMoreInteractions(categoryMapper);
    }

    @Test
    @DisplayName("生成的查询只选导航需要的列，并按 sort_order、id 升序")
    void findNavigation_buildsQueryOrderedBySortOrderThenId() {
        when(categoryMapper.selectList(anyWrapper())).thenReturn(List.of());

        categoryService.findNavigation();

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Wrapper<Category>> captor = ArgumentCaptor.forClass(Wrapper.class);
        verify(categoryMapper).selectList(captor.capture());
        Wrapper<Category> wrapper = captor.getValue();

        assertThat(wrapper.getSqlSelect()).isEqualTo("id,code,parent_id,name");
        assertThat(wrapper.getSqlSegment()).contains("sort_order ASC").contains("id ASC");
        assertThat(wrapper.getSqlSegment().indexOf("sort_order ASC"))
                .isLessThan(wrapper.getSqlSegment().indexOf("id ASC"));
    }

    @Test
    @DisplayName("一级分类没有二级分类时 children 为空列表而不是 null")
    void findNavigation_parentWithoutChildren_returnsEmptyChildren() {
        when(categoryMapper.selectList(anyWrapper())).thenReturn(List.of(
                category(3L, "HIST", null, "历史人文", 3)));

        List<CategoryNavigation> navigation = categoryService.findNavigation();

        assertThat(navigation).hasSize(1);
        assertThat(navigation.get(0).children()).isEmpty();
    }

    @Test
    @DisplayName("分类表为空时返回空列表")
    void findNavigation_noCategories_returnsEmptyList() {
        when(categoryMapper.selectList(anyWrapper())).thenReturn(List.of());

        assertThat(categoryService.findNavigation()).isEmpty();
    }

    @Test
    @DisplayName("Mapper 抛出 DataAccessResourceFailureException 时应继续抛出同一个异常")
    void findNavigation_whenMapperThrows_propagatesSameException() {
        DataAccessResourceFailureException failure =
                new DataAccessResourceFailureException("db unavailable");
        when(categoryMapper.selectList(anyWrapper())).thenThrow(failure);

        assertThatThrownBy(() -> categoryService.findNavigation())
                .isInstanceOf(DataAccessResourceFailureException.class)
                .isSameAs(failure);
    }

    /**
     * 匹配任意条件包装器；用泛型方法代替 any(Wrapper.class) 的原始类型写法，避免未检查转换告警。
     */
    private static Wrapper<Category> anyWrapper() {
        return any();
    }

    private static Category category(Long id, String code, Long parentId, String name, Integer sortOrder) {
        Category category = new Category();
        category.setId(id);
        category.setCode(code);
        category.setParentId(parentId);
        category.setName(name);
        category.setSortOrder(sortOrder);
        return category;
    }
}