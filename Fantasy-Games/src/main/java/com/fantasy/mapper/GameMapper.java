package com.fantasy.mapper;

import com.fantasy.entity.Category;
import com.fantasy.entity.Game;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fantasy.entity.Keyword;
import com.fantasy.entity.Tag;
import com.fantasy.model.vo.BlogInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Fantasy0521
 * @since 2024-12-31
 */
@Mapper
public interface GameMapper extends BaseMapper<Game> {

    List<Game> getGameInfoListByCategoryNameAndIsPublished(String categoryName,String keyword);

    List<Game> getGamesByKeyWords(@Param(value = "keywords") List<Keyword> keywords,@Param(value = "categories") List<Category> categories,@Param(value = "tags") List<Tag> tags);
}
