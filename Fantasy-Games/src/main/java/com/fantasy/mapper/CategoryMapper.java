package com.fantasy.mapper;

import com.fantasy.entity.Category;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fantasy.entity.Keyword;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Fantasy0521
 * @since 2023-03-03
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    List<Category> getCategoryByKeyWords(List<Keyword> keywords);
}
