package com.fantasy.service;

import com.fantasy.entity.Game;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fantasy.model.Result.PageResult;
import com.fantasy.model.vo.GameInfo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Fantasy0521
 * @since 2024-12-31
 */
public interface IGameService extends IService<Game> {

    PageResult<Game> getGameInfoListByCategoryNameAndIsPublished(String categoryName,String keyword, Integer pageNum,Integer pageSize);

    PageResult<GameInfo> getAllGamesByPage(Integer pageNum, Integer pageSize);

    GameInfo getGameById(Long id);
}
