package com.fantasy.controller.admin;


import com.fantasy.entity.Category;
import com.fantasy.model.Result.PageResult;
import com.fantasy.model.Result.Result;
import com.fantasy.model.vo.GameInfo;
import com.fantasy.service.ICategoryService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author Fantasy0521
 * @since 2023-03-03
 */
@RestController
@RequestMapping("admin/fantasy/category")
public class CategoryController {

    @Autowired
    private ICategoryService categoryService;
    @GetMapping("/categories")
    @ApiOperation(value = "访问分类页面",notes = "1")
    public Result categories(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "5") Integer pageSize){
        PageResult<Category> result = categoryService.getAllCategoriesByPage(pageNum,pageSize);
        return Result.ok("获取所有游戏信息成功!",result);
    }

    @PostMapping
    @ApiOperation(value = "访问分类页面",notes = "2")
    public Result addCategory(@RequestBody Category category){
        categoryService.save(category);
        return Result.ok("添加成功");
    }

    @PutMapping
    @ApiOperation(value = "访问分类页面",notes = "3")
    public Result editCategory(@RequestBody Category category){
        categoryService.updateById(category);
        return Result.ok("更新成功");
    }

    @DeleteMapping
    @ApiOperation(value = "访问分类页面",notes = "4")
    public Result deleteCategoryById(Integer id){
        categoryService.removeById(id);
        return Result.ok("删除成功");
    }

}

