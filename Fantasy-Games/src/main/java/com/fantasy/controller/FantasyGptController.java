package com.fantasy.controller;

import com.fantasy.entity.Answer;
import com.fantasy.model.Result.Result;
import com.fantasy.service.IFantasyGptService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/fantasy/gpt")
public class FantasyGptController {

    @Autowired
    private IFantasyGptService fantasyGptService;

    @GetMapping("/questAI")
    @ApiOperation(value = "questAI",notes = "1")
    public Result questAI(String question){
        Answer result = fantasyGptService.questAI(question,null);
        return Result.ok("success",result);
    }

    @PostMapping("/retry")
    @ApiOperation(value = "retry",notes = "1")
    public Result retry(Long answerId, String question, @RequestBody List<Answer> excludeAnswers){
        Answer result = fantasyGptService.retry(answerId,excludeAnswers, question);
        return Result.ok("success",result);
    }

    @GetMapping("/pick")
    @ApiOperation(value = "pick",notes = "1")
    public Result pick(Long answerId){
        fantasyGptService.pick(answerId);
        return Result.ok("success");
    }


}
