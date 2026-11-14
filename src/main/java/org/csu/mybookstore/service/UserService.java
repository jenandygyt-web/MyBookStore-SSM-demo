package org.csu.mybookstore.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.csu.mybookstore.dto.UserReadResult;
import org.csu.mybookstore.entity.User;
import org.csu.mybookstore.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 用户查询服务：基于 UserMapper 提供只读的用户信息查询，不涉及 HTTP 与登录逻辑。
 */
@Service
public class UserService {

    private final UserMapper userMapper;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * 按登录名查询用户展示信息。
     * 使用 Lambda 条件查询，只select五个展示字段，不读取密码散列；
     * 用户不存在返回空 Optional，数据访问异常继续向上抛出。
     */
    public Optional<UserReadResult> findByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(User::getId, User::getUsername, User::getDisplayName, User::getRole, User::getStatus)
                .eq(User::getUsername, username);
        User user = userMapper.selectOne(wrapper);
        return Optional.ofNullable(user)
                .map(u -> new UserReadResult(
                        u.getId(), u.getUsername(), u.getDisplayName(), u.getRole(), u.getStatus()));
    }

}
