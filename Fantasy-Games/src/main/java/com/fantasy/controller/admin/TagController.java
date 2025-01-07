package com.fantasy.controller.admin;


import com.fantasy.entity.Tag;
import com.fantasy.model.Result.PageResult;
import com.fantasy.model.Result.Result;
import com.fantasy.service.ITagService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Fantasy0521
 * @since 2023-03-03
 */
@RestController
@RequestMapping("admin/fantasy/tag")
public class TagController {

    @Autowired
    private ITagService tagService;
    
    @GetMapping("/tags")
    @ApiOperation(value = "访问标签页面",notes = "1")
    public Result tags(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "5") Integer pageSize){
        PageResult<Tag> result = tagService.getAllTagsByPage(pageNum,pageSize);
        return Result.ok("获取标签信息成功!",result);
    }

    @PostMapping
    @ApiOperation(value = "添加标签",notes = "2")
    public Result addTag(@RequestBody Tag tag){
        tagService.save(tag);
        return Result.ok("添加成功");
    }

    @PutMapping
    @ApiOperation(value = "更新标签",notes = "3")
    public Result editTag(@RequestBody Tag tag){
        tagService.updateById(tag);
        return Result.ok("更新成功");
    }

    @DeleteMapping
    @ApiOperation(value = "删除标签",notes = "4")
    public Result deleteTagById(Integer id){
        tagService.removeById(id);
        return Result.ok("删除成功");
    }


}

