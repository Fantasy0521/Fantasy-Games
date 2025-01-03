package com.fantasy.mapper;

import com.fantasy.entity.Game;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fantasy.model.vo.BlogInfo;
import org.apache.ibatis.annotations.Mapper;

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
}
