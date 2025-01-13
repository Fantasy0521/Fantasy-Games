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
    public PageResult<GameInfo> getAllGamesByPage(Integer pageNum, Integer pageSize) {
        //1 构建分页对象
        Page<Game> pageInfo = new Page<>(pageNum, pageSize);

        //2 创建LambdaQueryWrapper
        LambdaQueryWrapper<Game> queryWrapper = new LambdaQueryWrapper<>();

        //3 添加排序条件 is_top desc, create_time desc
        queryWrapper.orderByDesc(Game::getIsTop, Game::getCreateTime);

        //4 开始分页查询
        Page<Game> gamePage = this.page(pageInfo, queryWrapper);

        //5 转为GameInfo 返回页面数据
        Page<GameInfo> gameInfoPage = new Page<>();
        //对GameInfo进行处理
        extracted(gamePage, gameInfoPage);

        //6 封装为前端规定的PageResult
        // 此处getPages需要加一
        PageResult<GameInfo> result = new PageResult<>((int) gameInfoPage.getPages() + 1, gameInfoPage.getRecords());

        return result;
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
    public List<Game> getGamesByKeyWords(List<Keyword> keywords, List<Category> categories, List<Tag> tags) {
        return gameMapper.getGamesByKeyWords(keywords,categories,tags);
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
     * @param gamePage
     * @param gameInfoPage
     */
    private void extracted(Page<Game> gamePage, Page<GameInfo> gameInfoPage) {
        //1 对象拷贝
        BeanUtils.copyProperties(gamePage, gameInfoPage, "records");
        //对records进行处理
        List<Game> records = gamePage.getRecords();
        List<GameInfo> list = records.stream().map(this::gameToGameInfo).collect(Collectors.toList());
        gameInfoPage.setRecords(list);
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
