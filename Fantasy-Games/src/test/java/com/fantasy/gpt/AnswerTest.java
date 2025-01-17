package com.fantasy.gpt;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fantasy.entity.Answer;
import com.fantasy.entity.Game;
import com.fantasy.entity.Keyword;
import com.fantasy.mapper.AnswerMapper;
import com.fantasy.model.dto.AnswerDto;
import com.fantasy.service.IGameService;
import com.fantasy.service.impl.FantasyGptServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class AnswerTest {

    @Autowired
    private AnswerMapper answerMapper;

    @Autowired
    private IGameService gameService;

    @Autowired
    private FantasyGptServiceImpl fantasyGptService;

    @Test
    public void test(){
        List<AnswerDto> answersByKeyWords = answerMapper.getAnswersByKeyWords(null, null);
        System.out.println(answersByKeyWords);
    }

    /**
     * 重新计算回答的推荐分
     */
    @Test
    public void calculateRecommendScore(){
        List<Answer> answers = answerMapper.selectList(null);
        for (Answer answer : answers) {
            try {
//                String content = answer.getContent();
//                String name = content.substring(content.indexOf("：") + 1, content.indexOf("。"));
//                List<Game> list = gameService.list(new LambdaQueryWrapper<Game>().eq(Game::getName, name));
//                if (!list.isEmpty()) {
//                    answer.setGameId(list.get(0).getId());
//                }
                Double recommendScore = fantasyGptService.calculateRecommendScore(answer.getGameId());
                answer.setRecommendScore(recommendScore);
//                answerMapper.updateById(answer);
            }catch (Exception e){
            }
        }
        System.out.println(answers);
    }


}
