package com.fantasy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fantasy.entity.*;
import com.fantasy.entity.Game;
import com.fantasy.exception.BizException;
import com.fantasy.mapper.GameMapper;
import com.fantasy.mapper.TagMapper;
import com.fantasy.model.Result.PageResult;
import com.fantasy.model.Result.Result;
import com.fantasy.model.vo.GameInfo;
import com.fantasy.model.vo.GameInfo;
import com.fantasy.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fantasy.util.StringUtils;
import com.fantasy.util.markdown.MarkdownUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Fantasy0521
 * @since 2024-12-31
 */
@Service
public class GameServiceImpl extends ServiceImpl<GameMapper, Game> implements IGameService {

    @Autowired
    private GameMapper gameMapper;

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private ITagService tagService;

    @Autowired
    private IGameImageService gameImageService;

    @Autowired
    private IBlogTagService blogTagService;

    @Value("${fileUpload.uploadUrl}")
    private String uploadUrl;

    @Value("${fileUpload.downloadUrl}")
    private String downloadUrl;

    private static final String orderBy = "is_top desc, create_time desc";

    @Override
    public PageResult<Game> getGameInfoListByCategoryNameAndIsPublished(String categoryName,String keyword, Integer pageNum,Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize, orderBy);
        List<Game> games = gameMapper.getGameInfoListByCategoryNameAndIsPublished(categoryName,keyword);
        //需要对Game进行MarkDown处理
        for (Game game : games) {
            game.setDescription(MarkdownUtils.markdownToHtml(game.getDescription()));
        }
        PageInfo<Game> pageInfo = new PageInfo<>(games);
        PageResult<Game> pageResult = new PageResult<>(pageInfo.getPages(), pageInfo.getList());
        return pageResult;
    }

    @Override
    public PageInfo<GameInfo> getAllGamesByPage(Integer pageNum, Integer pageSize,Long categoryId,String title) {
        //1 构建分页对象
        PageHelper.startPage(pageNum, pageSize, "is_top desc, is_recommend desc, views desc, stars desc, create_time desc");
        LambdaQueryWrapper<Game> queryWrapper = new LambdaQueryWrapper<>();
        if (categoryId != null) {
            queryWrapper.eq(Game::getCategoryId, categoryId);
        }
        if (!StringUtils.isEmpty(title)) {
            queryWrapper.like(Game::getName, title);
        }
        List<Game> list = list(queryWrapper);
        PageInfo<Game> gamePageInfo = new PageInfo<>(list);
        //对GameInfo进行处理
        List<GameInfo> gameInfos = gameListToGameInfoList(list);
        PageInfo<GameInfo> gameInfoPageInfo = new PageInfo<>(gameInfos);
        //转化后需要重新设置分页信息
        gameInfoPageInfo.setPages(gamePageInfo.getPages());
        gameInfoPageInfo.setTotal(gamePageInfo.getTotal());
        return gameInfoPageInfo;
    }

    @Override
    public GameInfo getGameById(Long id) {
        Game byId = getById(id);
        return gameToGameInfo(byId);
    }

    @Transactional
    @Override
    public void saveGame(GameInfo gameInfo) {
        //1 保存游戏主表
        Game game = new Game();
        BeanUtils.copyProperties(gameInfo, game);
        handlerDate(gameInfo, game);
        game.setCreateTime(LocalDateTime.now());
        save(game);

        //保存其他信息
        gameInfo.setId(game.getId());
        saveGameInfo(gameInfo);

    }

    @Override
    @Transactional
    public void updateGame(GameInfo gameInfo) {
        //1 更新游戏主表
        Game game = new Game();
        BeanUtils.copyProperties(gameInfo, game);
        handlerDate(gameInfo, game);
        game.setUpdateTime(LocalDateTime.now());
        updateById(game);

        //2 更新其他信息
        saveGameInfo(gameInfo);
    }

    @Override
    @Transactional
    public void deleteGame(Long id) {
        //1 删除游戏主表
        removeById(id);

        //2 删除游戏图片表
        gameImageService.removeByIds(gameImageService.list(new LambdaQueryWrapper<GameImage>().eq(GameImage::getGameId, id)));

        //3 删除游戏标签表
        blogTagService.deleteGameTags(id);
    }

    @Override
    public void updateGameTopById(Long id, Boolean top) {
        LambdaUpdateWrapper<Game> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Game::getIsTop, top);
        updateWrapper.eq(Game::getId, id);
        this.update(updateWrapper);
    }

    @Override
    public void updateGameRecommendById(Long id, Boolean recommend) {
        LambdaUpdateWrapper<Game> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Game::getIsRecommend, recommend);
        updateWrapper.eq(Game::getId, id);
        this.update(updateWrapper);
    }

    @Override
    public List<Game> getGamesByKeyWords(List<Keyword> keywords, List<Category> categories, List<Tag> tags,List<Long> excludeGameIds) {
        return gameMapper.getGamesByKeyWords(keywords,categories,tags,excludeGameIds);
    }

    private void handlerDate(GameInfo gameInfo,Game game){
        String publishDateString = gameInfo.getPublishDateString();
        if (!StringUtils.isEmpty(publishDateString)) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                game.setPublishDate(LocalDateTime.ofInstant(simpleDateFormat.parse(publishDateString).toInstant(), ZoneId.systemDefault()));
            } catch (ParseException e) {
                throw new RuntimeException("日期格式错误");
            }
        }
    }

    private void saveGameInfo(GameInfo gameInfo) {
        if (gameInfo.getId() != null){
            //1 更新前删除子表
            gameImageService.removeByIds(gameImageService.list(new LambdaQueryWrapper<GameImage>().eq(GameImage::getGameId, gameInfo.getId())));
            blogTagService.deleteGameTags(gameInfo.getId());
        }
        //2 保存游戏图片表
        List<GameImage> gameImages = gameInfo.getGameImages();
        for (GameImage gameImage : gameImages) {
            gameImage.setUrl(downloadUrl + gameImage.getName());
            gameImage.setGameId(gameInfo.getId());
            gameImage.setCreateTime(LocalDateTime.now());
        }
        gameImageService.saveBatch(gameImages);

        //处理标签
        List<Tag> tagList = gameInfo.getTags();

        //关联游戏和标签(维护 game_tag 表)
        ArrayList<BlogTag> gameTags = new ArrayList<>();
        for (Tag t : tagList) {
            gameTags.add(new BlogTag(gameInfo.getId(), t.getId()));
        }
        if (!gameTags.isEmpty()){
            blogTagService.saveGameTag(gameTags);
        }
    }


    /**
     * 对GameInfo进行处理
     *
     * @param gameList
     */
    private List<GameInfo> gameListToGameInfoList(List<Game> gameList) {
        List<GameInfo> gameInfoList = new ArrayList<>();
        gameList.forEach(game -> {
            GameInfo gameInfo = gameToGameInfo(game);
            gameInfoList.add(gameInfo);
        });
        return gameInfoList;
    }

    private GameInfo gameToGameInfo(Game game) {
        GameInfo gameInfo = new GameInfo();
        //对象拷贝
        BeanUtils.copyProperties(game, gameInfo);

        //游戏图片
        List<GameImage> gameImages = gameImageService.list(new LambdaQueryWrapper<GameImage>().eq(GameImage::getGameId, game.getId()));
        gameInfo.setGameImages(gameImages);

        //2 对原本字段进行扩展
        //文章创建时间 LocalDateTime转 Date
        LocalDateTime createTime = game.getCreateTime();
        Date date = Date.from(createTime.atZone(ZoneId.systemDefault()).toInstant());
        gameInfo.setCreateTime(date);
        //使用MarkDown工具类处理md语法
        gameInfo.setDescription(MarkdownUtils.markdownToHtmlExtensions(gameInfo.getDescription()));

        //3 查询其他关联表数据
        //查询文章分类
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getId, game.getCategoryId());
        Category category = categoryService.getOne(queryWrapper);
        gameInfo.setCategory(category);
        //查询所属的标签
        // 此处需要用到中间表Game_Tag 多对多
        List<Tag> tagListById = tagMapper.getTagListByGameId(gameInfo.getId());
        gameInfo.setTags(tagListById);
        //查询
        return gameInfo;
    }
    
    
}
