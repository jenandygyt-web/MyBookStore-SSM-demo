package org.csu.mybookstore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.csu.mybookstore.entity.User;

/**
 * 用户 Mapper：继承 BaseMapper&lt;User&gt; 后即可使用 insert / selectById /
 * selectList / updateById / deleteById 等内置方法，无需手写 SQL。
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
