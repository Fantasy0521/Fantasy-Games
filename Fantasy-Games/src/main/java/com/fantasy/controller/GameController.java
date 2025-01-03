package com.fantasy.controller;


import com.fantasy.entity.Game;
import com.fantasy.model.Result.PageResult;
import com.fantasy.model.Result.Result;
import com.fantasy.model.vo.BlogDetail;
import com.fantasy.model.vo.BlogInfo;
import com.fantasy.model.vo.GameInfo;
import com.fantasy.service.IGameService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Fantasy0521
 * @since 2024-12-31
 */
@RestController
@RequestMapping("/fantasy/game")
public class GameController {

    @Autowired
    private IGameService gameService;

    /**
     * 根据分类名称分页查询公开的游戏列表
     * @param categoryName 分类名称
     * @param pageNum 页码
     * @return
     */
    @GetMapping("category")
    @ApiOperation(value = "查看分类",notes = "1")
    public Result category(@RequestParam String categoryName,
                           @RequestParam(required = false) String keyword,
                           @RequestParam(defaultValue = "1") Integer pageNum,@RequestParam(defaultValue = "5") Integer pageSize) {
        PageResult<Game> pageResult = gameService.getGameInfoListByCategoryNameAndIsPublished(categoryName,keyword, pageNum,pageSize);
        return Result.ok("请求成功", pageResult);
    }



}

