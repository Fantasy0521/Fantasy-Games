package com.fantasy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fantasy.entity.*;
import com.fantasy.entity.Game;
import com.fantasy.mapper.GameMapper;
import com.fantasy.mapper.TagMapper;
import com.fantasy.model.Result.PageResult;
import com.fantasy.model.vo.GameInfo;
import com.fantasy.model.vo.GameInfo;
import com.fantasy.service.ICategoryService;
import com.fantasy.service.IGameService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fantasy.util.markdown.MarkdownUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
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
