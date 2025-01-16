package com.fantasy.service;

import com.fantasy.entity.Category;
import com.fantasy.entity.Game;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fantasy.entity.Keyword;
import com.fantasy.entity.Tag;
import com.fantasy.model.Result.PageResult;
import com.fantasy.model.vo.GameInfo;
import com.github.pagehelper.PageInfo;

import java.util.List;

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

    PageInfo<GameInfo> getAllGamesByPage(Integer pageNum, Integer pageSize);

    GameInfo getGameById(Long id);

    void saveGame(GameInfo gameInfo);

    void updateGame(GameInfo gameInfo);

    void deleteGame(Long id);

    void updateGameTopById(Long id, Boolean top);

    void updateGameRecommendById(Long id, Boolean recommend);

    List<Game> getGamesByKeyWords(List<Keyword> keywords, List<Category> categories, List<Tag> tags,List<Long> excludeGameIds);
}
