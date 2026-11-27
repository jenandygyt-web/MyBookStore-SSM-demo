package org.csu.mybookstore.service;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.csu.mybookstore.dto.UserReadResult;
import org.csu.mybookstore.entity.User;
import org.csu.mybookstore.mapper.UserMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * UserService 纯单元测试：Mockito 模拟 UserMapper，构造真实 UserService，
 * 不启动 Spring / Web，也不连接数据库。
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    private UserService userService;

    /**
     * Lambda 条件查询会即时解析方法引用 -> 列名，需要 User 的 TableInfo 缓存。
     * 这里只为本测试类补齐必要初始化，不启动完整应用。
     */
    @BeforeAll
    static void initMybatisPlusMetadata() {
        MybatisConfiguration configuration = new MybatisConfiguration();
        GlobalConfig globalConfig = GlobalConfigUtils.defaults();
        GlobalConfigUtils.setGlobalConfig(configuration, globalConfig);
        MapperBuilderAssistant assistant = new MapperBuilderAssistant(configuration, "");
        TableInfoHelper.initTableInfo(assistant, User.class);
    }

    @BeforeEach
    void setUp() {
        userService = new UserService(userMapper);
    }

    @Test
    @DisplayName("查询到用户时应正确转换为 UserReadResult")
    void findByUsername_whenFound_mapsToUserReadResult() {
        User user = new User();
        user.setId(1L);
        user.setUsername("alice");
        user.setDisplayName("Alice");
        user.setRole("CUSTOMER");
        user.setStatus("ENABLED");
        user.setPassword("$2a$10$secretHashShouldNotLeak");

        when(userMapper.selectOne(any(Wrapper.class))).thenReturn(user);

        Optional<UserReadResult> result = userService.findByUsername("alice");

        assertThat(result).isPresent();
        UserReadResult read = result.get();
        assertThat(read.id()).isEqualTo(1L);
        assertThat(read.username()).isEqualTo("alice");
        assertThat(read.displayName()).isEqualTo("Alice");
        assertThat(read.role()).isEqualTo("CUSTOMER");
        assertThat(read.status()).isEqualTo("ENABLED");
    }

    @Test
    @DisplayName("查不到用户时应返回空 Optional")
    void findByUsername_whenNotFound_returnsEmptyOptional() {
        when(userMapper.selectOne(any(Wrapper.class))).thenReturn(null);

        Optional<UserReadResult> result = userService.findByUsername("ghost");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Mapper 抛出 DataAccessResourceFailureException 时应继续抛出同一个异常")
    void findByUsername_whenMapperThrows_propagatesSameException() {
        DataAccessResourceFailureException failure =
                new DataAccessResourceFailureException("db unavailable");
        when(userMapper.selectOne(any(Wrapper.class))).thenThrow(failure);

        assertThatThrownBy(() -> userService.findByUsername("alice"))
                .isInstanceOf(DataAccessResourceFailureException.class)
                .isSameAs(failure);
    }
}
