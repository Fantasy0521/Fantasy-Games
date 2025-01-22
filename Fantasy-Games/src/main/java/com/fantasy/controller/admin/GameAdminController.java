package com.fantasy.controller.admin;


import com.fantasy.entity.Category;
import com.fantasy.entity.Game;
import com.fantasy.entity.Tag;
import com.fantasy.model.Result.PageResult;
import com.fantasy.model.Result.Result;
import com.fantasy.model.vo.GameInfo;
import com.fantasy.service.ICategoryService;
import com.fantasy.service.IGameService;
import com.fantasy.service.ITagService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Fantasy0521
 * @since 2024-12-31
 */
@RestController
@RequestMapping("admin/fantasy/game")
public class GameAdminController {

    @Autowired
    private IGameService gameService;

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private ITagService tagService;

    /**
     * 按置顶、创建时间排序 分页查询博客简要信息列表
     * @param pageNum 页码
     * @return
     */
    @GetMapping("/games")
    @ApiOperation(value = "访问页面`",notes = "1")
    public Result getAllGames(@RequestParam(defaultValue = "1") Integer pageNum,@RequestParam(defaultValue = "5") Integer pageSize,
                              @RequestParam(required = false) Long categoryId,@RequestParam(required = false) String title){
        PageInfo<GameInfo> allGamesByPage = gameService.getAllGamesByPage(pageNum,pageSize,categoryId,title);
        return Result.ok("获取所有游戏信息成功!",allGamesByPage);
    }

    @GetMapping
    @ApiOperation(value = "查看游戏",notes = "1")
    public Result getOneGamesById(@RequestParam Long id){
        GameInfo gameById = gameService.getGameById(id);
        return Result.ok("获取游戏详情信息成功",gameById);
    }

    @PostMapping
    @ApiOperation(value = "新建游戏",notes = "1")
    public Result saveGame(@RequestBody GameInfo gameInfo){
        gameService.saveGame(gameInfo);
        return Result.ok("新建成功");
    }

    @PutMapping
    @ApiOperation(value = "更新游戏",notes = "1")
    public Result updateGame(@RequestBody GameInfo gameInfo){
        gameService.updateGame(gameInfo);
        return Result.ok("更新成功");
    }


    @DeleteMapping
    public Result deleteGameById(@RequestParam Long id){
        gameService.deleteGame(id);
        return Result.ok("删除成功");
    }

    /**
     * 获取分类列表和标签列表
     *
     * @return
     */
    @GetMapping("/categoryAndTag")
    public Result categoryAndTag() {
        List<Category> categories = categoryService.list();
        List<Tag> tags = tagService.list();
        Map<String, Object> map = new HashMap<>();
        map.put("categories", categories);
        map.put("tags", tags);
        return Result.ok("请求成功", map);
    }

    @PutMapping("/top")
    public Result updateTop(@RequestParam Long id, @RequestParam Boolean top) {
        gameService.updateGameTopById(id, top);
        return Result.ok("操作成功");
    }

    @PutMapping("/recommend")
    public Result updateRecommend(@RequestParam Long id, @RequestParam Boolean recommend) {
        gameService.updateGameRecommendById(id, recommend);
        return Result.ok("操作成功");
    }



}

