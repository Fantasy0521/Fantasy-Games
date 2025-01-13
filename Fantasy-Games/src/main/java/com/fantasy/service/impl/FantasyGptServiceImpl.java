package com.fantasy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fantasy.entity.*;
import com.fantasy.service.*;
import com.fantasy.util.TongYiUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FantasyGptServiceImpl implements IFantasyGptService {

    @Autowired
    private TongYiUtil tongYiUtil;

    @Autowired
    private IKeywordService keywordService;

    @Autowired
    private IQuestionService questionService;

    @Autowired
    private IAnswerService answerService;

    @Autowired
    private IGameService gameService;

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private ITagService tagService;

    /**
     * Fantasy-Gpt AI for Game
     * 用户输入关键词后，系统会根据关键词匹配度和推荐分计算最终分值，并推荐最终分值最高的游戏。用户可以对推荐结果进行反馈，从而不断优化推荐算法。
     * @param question
     * @param answerId 被用户否定的回答id
     * @return
     */
    @Transactional
    @Override
    public Answer questAI(String question,Long answerId) {
        if (question == null || question.length() <= 1) {
            throw new RuntimeException("请输入正确的信息");
        }
        String result = null;
        //1 检查用户输入的问题是否已经在数据库中有相同的问题记录，有则直接根据question_id取出回答
        List<Question> baseQuestions = questionService.list(new LambdaQueryWrapper<Question>().eq(Question::getContent, question).orderByDesc(Question::getCreateTime));
        Question baseQuestion = !baseQuestions.isEmpty() ? baseQuestions.get(0) : null;
        if (baseQuestion != null){
            Answer answer = answerService.getAnswerByQuestionId(baseQuestion.getId());
            if (answer != null && answer.getId() != answerId) {
                return answer;
            }
        }

        Question question1 = baseQuestion;
        if (question1 == null) {// 新问题
            question1 = initQuestion(question);
        }
        //2 没有相同的问题记录，这时候先从问题中提取关键词和对应的权重
        // 通过TF-IDF算法获取用户输入的关键词
//        List<Keyword> keywords = keywordService.getKeyWordsByQuestContent(question);
        Map<String, List<Keyword>> keyWordMap = keywordService.getKeyWordsAndNewWordsByQuestContent(question);
        List<Keyword> keywordList = keyWordMap.get("keywords");
        List<Keyword> newKeywordList = keyWordMap.get("newKeyWords");
        List<Keyword> newWordList = keyWordMap.get("newWords");
        // 出现问题 newWordList中的新词(可能的关键词)不加入newKeywordList就永远不会成为关键词,因此还需要保存新词newWord
        // 将newWord添加到newKeywordList中
        newKeywordList.addAll(newWordList);
        //所有关键词
        List<Keyword> keywords = keywordList;
        keywords.addAll(newKeywordList);

        //3 在数据库中查找相同的关键词，通过关键词获取到所有的回答，通过关键词的权重和回答的认可度计算出最符合的一个回答
        if (!keywordList.isEmpty()) {
            // 获取关键词对应的回答
            List<Answer> answers = answerService.getAnswersByKeyWords(keywordList,answerId);
            // 计算关键词的权重和回答的认可度计算出最符合的一个回答
            Answer answer = getBestAnswer(keywords, answers);
            if (answer != null) {
                //返回回答前先保存问题和新产生的关键词
                afterSaveHandler(question1,keywordList,newKeywordList,answer);
                return answer;
            }
        }

        //4 如果通过关键词也未找到已有回答，开始根据问题生成回答，从游戏库中找到符合用户关键词的游戏，通用语句+对应游戏 =》回答 。 回答和关键词存入数据库
        Answer answer = new Answer();

        if (answerId == null){//被否定后就不再从游戏库中生成回答，直接调用通义api生成回答
            //检查关键词中的游戏类型，如果存在则添加分类过滤条件
            List<Category> categories = categoryService.getCateGoryByKeyWords(keywords);
            //检查关键词中的游戏标签，如果存在则添加标签过滤条件
            List<Tag> tags = tagService.getTagsByKeyWords(keywords);

            List<Game> games = gameService.getGamesByKeyWords(keywords,categories,tags);
            if (!games.isEmpty()) {
                Game game = getBestGame(games,keywords);
                result = "为您从游戏库中找到最合适的一款游戏：" + game.getName() + "。" + game.getDescription();
                answer = initAnswer(result,false);
            }
        }

        if (result == null) {
            //5 如果在游戏库中未找到符合用户关键词的游戏，此时直接调用通义api生成回答，回答和关键词存入数据库
            result = tongYiUtil.questAI(question);
            //设置默认的推荐分值和最终分值
            answer = initAnswer(result,true);
        }
        answer.setQuestionId(question1.getId());

        //6 异步后处理 保存问题、关键词、回答到数据库
        afterSaveHandler(question1,keywordList,newKeywordList,answer);
        return answer;
    }


    @Async
    protected void afterSaveHandler(Question question, List<Keyword> keywordList, List<Keyword> newKeywordList, Answer answer) {
        questionService.saveOrUpdate(question);
        answer.setQuestionId(question.getId());
        answerService.saveOrUpdate(answer);
        keywordService.saveKeyWords(newKeywordList,question.getId(),answer.getId());
        keywordService.updateBatchById(keywordList);
    }

    /**
     * 计算最好的回答
     * @param keywords
     * @param answers
     * @return
     */
    private Answer getBestAnswer(List<Keyword> keywords, List<Answer> answers) {
        //todo 根据关键词权重和匹配度计算最好的回答
        Answer bestAnswer = null;
        Double bestFinalScore = 0.0;
        if (!answers.isEmpty()) {
//            for (Answer answer : answers){
//                Double finalScore = calculateFinalScore(answer,keywords);
//                if (finalScore > bestFinalScore){
//                    bestFinalScore = finalScore;
//                    bestAnswer = answer;
//                }
//            }
            return answers.get(0);
        }
        return null;
    }

    /**
     * 计算推荐分值和最终分值
     * @param answer
     * @param keywords
     * @return
     */
    private Double calculateFinalScore(Answer answer,List<Keyword> keywords) {
        // todo 最终分 = 认可度系数 * 认可度 * 推荐分系数 * 推荐分值 * 关键词匹配度
        //对应的系数从数据库推荐算法配置表获取
        Double recommendScore = calculateRecommendScore(answer);

        return null;
    }

    private Double calculateRecommendScore(Answer answer) {
        // todo 推荐分受游戏推荐指数影响  , 可能需要加上game_id字段
        return null;
    }

    /**
     * 计算最好的游戏
     * @param games
     * @return
     */
    private Game getBestGame(List<Game> games,List<Keyword> keywords) {
        //这里同一个游戏可能出现多次，出现次数越多所匹配的关键字也就越多，优先返回与关键词匹配度最高的游戏
        //如果list中的所有游戏出现次数相同，则返回第一个游戏，即推荐度最高的游戏
        //这个逻辑我选择在sql中处理，这里只需要取第一个游戏就行
        return games.get(0);
    }

    @Override
    public Answer retry(Long answerId,String question) {
        //提高answer的认可度并重新计算最终分值
        Answer answer = answerService.getById(answerId);
        if (answer != null) {
            answer.setAcceptanceCount(answer.getAcceptanceCount() - 1);
            answerService.updateById(answer);
        }
        return questAI(question,answerId);
    }

    @Override
    public void pick(Long answerId) {
        //提高answer的认可度并重新计算最终分值
        Answer answer = answerService.getById(answerId);
        if (answer != null) {
            answer.setAcceptanceCount(answer.getAcceptanceCount() + 1);
            answerService.updateById(answer);
        }
    }

    private Question initQuestion(String content){
        Question question = new Question();
        question.setContent(content);
        question.setQuestCount(1);
        question.setCreateTime(LocalDateTime.now());
        question.setUserId(1L);
        return question;
    }

    private Answer initAnswer(String result,Boolean isTongyi){
        Answer answer = new Answer();
        answer.setContent(result);
        answer.setRecommendScore(0.5);
        answer.setFinalScore(0.5);
        answer.setAcceptanceCount(1);
        answer.setIsTongyi(isTongyi);
        answer.setCreateTime(LocalDateTime.now());
        return answer;
    }


}
