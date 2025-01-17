package com.fantasy.controller.admin;

import com.fantasy.entity.Question;
import com.fantasy.model.Result.Result;
import com.fantasy.model.vo.QuestionVo;
import com.fantasy.service.IQuestionService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("admin/fantasy/question")
public class QuestionAdminController {

    @Autowired
    private IQuestionService questionService;

    @GetMapping("/questions")
    public Result getQuestions(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "5") Integer pageSize) {
        PageInfo<QuestionVo> pageResult = questionService.getAllQuestionsByPage(pageNum,pageSize);
        return Result.ok("获取问题成功!",pageResult);
    }

    @PutMapping
    public Result updateQuestion(@RequestBody Question question) {
        questionService.updateById(question);
        return Result.ok("更新问题成功!");
    }

    @DeleteMapping
    public Result deleteQuestion(@RequestParam Long id) {
//        questionService.deleteQuestion(id);
        return Result.ok("删除问题成功!");
    }

    @PostMapping
    public Result saveQuestion(@RequestBody Question question) {
        question.setCreateTime(LocalDateTime.now());
        questionService.save(question);
        return Result.ok("添加问题成功!");
    }
    
}
