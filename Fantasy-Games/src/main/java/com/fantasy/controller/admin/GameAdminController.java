package com.fantasy.controller.admin;


import com.fantasy.model.Result.PageResult;
import com.fantasy.model.Result.Result;
import com.fantasy.model.vo.GameInfo;
import com.fantasy.service.IGameService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 按置顶、创建时间排序 分页查询博客简要信息列表
     * @param pageNum 页码
     * @return
     */
    @GetMapping("/games")
    @ApiOperation(value = "访问页面`",notes = "1")
    public Result getAllGames(@RequestParam(defaultValue = "1") Integer pageNum,@RequestParam(defaultValue = "5") Integer pageSize){
        PageResult<GameInfo> allGamesByPage = gameService.getAllGamesByPage(pageNum,pageSize);
        return Result.ok("获取所有游戏信息成功!",allGamesByPage);
    }

    @GetMapping
    @ApiOperation(value = "查看游戏",notes = "1")
    public Result getOneGamesById(@RequestParam Long id){
        GameInfo gameById = gameService.getGameById(id);
        return Result.ok("获取游戏详情信息成功",gameById);
    }

    @DeleteMapping
    public Result deleteGameById(@RequestParam Long id){
        boolean b = gameService.removeById(id);
        if (b){
            return Result.ok("删除成功");
        }
        return Result.error("删除失败");
    }




}

