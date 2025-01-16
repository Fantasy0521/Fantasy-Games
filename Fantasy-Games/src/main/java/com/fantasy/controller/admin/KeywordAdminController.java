package com.fantasy.controller.admin;

import com.fantasy.entity.Keyword;
import com.fantasy.model.Result.PageResult;
import com.fantasy.model.Result.Result;
import com.fantasy.service.IKeywordService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("admin/fantasy/keyword")
public class KeywordAdminController {

    @Autowired
    private IKeywordService keywordService;

    @GetMapping("/keywords")
    public Result getKeywords(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "5") Integer pageSize) {
        PageInfo<Keyword> pageResult = keywordService.getAllKeywordsByPage(pageNum,pageSize);
        return Result.ok("获取关键词成功!",pageResult);
    }

    @PutMapping
    public Result updateKeyword(@RequestBody Keyword keyword) {
        keywordService.updateById(keyword);
        return Result.ok("更新关键词成功!");
    }

    @DeleteMapping
    public Result deleteKeyword(@RequestParam Long id) {
        keywordService.deleteKeyword(id);
        return Result.ok("删除关键词成功!");
    }

    @PostMapping
    public Result saveKeyword(@RequestBody Keyword keyword) {
        keyword.setCreateTime(LocalDateTime.now());
        keywordService.save(keyword);
        return Result.ok("添加关键词成功!");
    }

}
