package com.fantasy.gpt;

import com.fantasy.model.vo.QuestionVo;
import com.fantasy.service.IQuestionService;
import com.github.pagehelper.PageInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class QuestionTest {

    @Autowired
    private IQuestionService questionService;

    @Test
    public void test(){
        PageInfo<QuestionVo> allQuestionsByPage = questionService.getAllQuestionsByPage(1, 20);
        System.out.println(allQuestionsByPage.getList());
    }

}
