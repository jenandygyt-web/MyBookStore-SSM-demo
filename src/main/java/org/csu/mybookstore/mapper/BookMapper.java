package org.csu.mybookstore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.csu.mybookstore.entity.Book;

/**
 * 图书 Mapper：继承 BaseMapper 后无需写任何 SQL，
 * 即可使用 insert / selectById / selectList / updateById / deleteById 等内置方法。
 */
@Mapper
public interface BookMapper extends BaseMapper<Book> {

}
