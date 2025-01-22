package com.fantasy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fantasy.entity.*;
import com.fantasy.mapper.CalculateConfigMapper;
import com.fantasy.model.dto.AnswerDto;
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
import java.util.Objects;
import java.util.stream.Collectors;

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

    @Autowired
    private CalculateConfigMapper calculateConfigMapper;

    /**
     * Fantasy-Gpt AI for Game
     * 用户输入关键词后，系统会根据关键词匹配度和推荐分计算最终分值，并推荐最终分值最高的游戏。用户可以对推荐结果进行反馈，从而不断优化推荐算法。
     * @param question
     * @param excludeAnswers 被用户否定的回答列表
     * @return
     */
    @Transactional
    @Override
    public Answer questAI(String question,List<Answer> excludeAnswers) {
        if (question == null || question.length() <= 1) {
            throw new RuntimeException("请输入正确的信息");
        }
        String result = null;
        //1 检查用户输入的问题是否已经在数据库中有相同的问题记录，有则直接根据question_id取出回答
        List<Question> baseQuestions = questionService.list(new LambdaQueryWrapper<Question>().eq(Question::getContent, question).orderByDesc(Question::getCreateTime));
        Question baseQuestion = !baseQuestions.isEmpty() ? baseQuestions.get(0) : null;
        if (baseQuestion != null){
            Answer answer = answerService.getAnswerByQuestionId(baseQuestion.getId(),excludeAnswers);
            if (answer != null) {
                return answer;
            }
        }

        Question question1 = baseQuestion;
        if (question1 == null) {// 新问题
            question1 = initQuestion(question);
        }
        //2 没有相同的问题记录，这时候先从问题中提取关键词和对应的权重
        // 通过TF-IDF算法获取用户输入的关键词
        Map<String, List<Keyword>> keyWordMap = keywordService.getKeyWordsAndNewWordsByQuestContent(question);
        List<Keyword> keywordList = keyWordMap.get("keywords");
        //新词(可能的关键词)需要作为暂定的关键词存入数据库,后续根据定时任务统计词频将高频新词设为关键词
        List<Keyword> newWordList = keyWordMap.get("newWords");
        //所有关键词
        List<Keyword> words = keywordList;
        if (words.isEmpty()){
            words = newWordList;
        }else {
            words.addAll(newWordList);
        }

        //3 在数据库中查找相同的关键词，通过关键词获取到所有的回答，通过关键词的权重和回答的认可度计算出最符合的一个回答
        if (!keywordList.isEmpty()) {
            // 获取关键词对应的回答
            List<AnswerDto> answers = answerService.getAnswersByKeyWords(keywordList,excludeAnswers);
            // 计算关键词的权重和回答的认可度计算出最符合的一个回答
            Answer answer = getBestAnswer(keywordList, answers);
            if (answer != null) {
                //返回回答前先保存问题和新产生的关键词
                //这里的answer也要新保存一份,因为要保存question_id,每一个不同的问题对应一条回答记录(即使回答内容相同),方便后续对回答的认可度进行修改
                if (!answer.getQuestionId().equals(question1.getId())){
                    answer.setId(null);
                }
                afterSaveHandler(question1,keywordList,newWordList,answer);
                return answer;
            }
        }

        //4 如果通过关键词也未找到已有回答，开始根据问题生成回答，从游戏库中找到符合用户关键词的游戏，通用语句+对应游戏 =》回答 。 回答和关键词存入数据库
        Answer answer = new Answer();

        if (!words.isEmpty() && (excludeAnswers == null || excludeAnswers.size() < 2)){//被否定2次以上后就不再从游戏库中生成回答，直接调用通义api生成回答
            //检查关键词中的游戏类型，如果存在则添加分类过滤条件
            List<Category> categories = categoryService.getCateGoryByKeyWords(words);
            //检查关键词中的游戏标签，如果存在则添加标签过滤条件
            List<Tag> tags = tagService.getTagsByKeyWords(words);

            // 排除excludeAnswers中的游戏id
            List<Long> excludeGameIds = null;
            if (excludeAnswers != null) {
                excludeGameIds = excludeAnswers.stream().map(Answer::getGameId).filter(Objects::nonNull).collect(Collectors.toList());
            }
            List<Game> games = gameService.getGamesByKeyWords(words,categories,tags,excludeGameIds);
            if (!games.isEmpty()) {
                Game game = getBestGame(games,keywordList);
                result = "为您从游戏库中找到最合适的一款游戏：" + game.getName() + "。" + game.getDescription();
                answer = initAnswer(result,false,game.getId());
            }
        }

        if (result == null) {
            //5 如果在游戏库中未找到符合用户关键词的游戏，此时直接调用通义api生成回答，回答和关键词存入数据库
            result = tongYiUtil.questAI(question);
            //如果result长度大于200，只取前200字
            if (result.length() > 200) {
                result = result.substring(0, 200);
            }
            //设置默认的推荐分值和最终分值
            answer = initAnswer(result,true,null);
        }
        answer.setQuestionId(question1.getId());
        //计算推荐分值
        Double recommendScore = calculateRecommendScore(answer.getGameId());
        answer.setRecommendScore(recommendScore);

        //6 异步后处理 保存问题、关键词、回答到数据库
        afterSaveHandler(question1,keywordList,newWordList,answer);
        return answer;
    }


    @Async
    protected void afterSaveHandler(Question question, List<Keyword> keywordList, List<Keyword> newKeywordList, Answer answer) {
        questionService.saveOrUpdate(question);
        answer.setQuestionId(question.getId());
        answerService.saveOrUpdate(answer);
        keywordService.updateBatchById(keywordList);
        keywordService.saveKeyWordsAndSub(newKeywordList,question.getId(),answer.getId());
        //只保存keywordList的关联表信息
        keywordService.saveKeyWordsSub(keywordList,question.getId(),answer.getId());
    }

    /**
     * 计算最好的回答
     * @param keywords
     * @param answers
     * @return
     */
    private Answer getBestAnswer(List<Keyword> keywords, List<AnswerDto> answers) {
        //根据关键词权重和匹配度计算最好的回答
        Answer bestAnswer = null;
        Double bestFinalScore = 0.0;
        if (!answers.isEmpty()) {
            for (AnswerDto answer : answers){
                Double finalScore = calculateFinalScore(answer,keywords);
                if (finalScore > bestFinalScore){
                    bestFinalScore = finalScore;
                    bestAnswer = answer;
                }
            }
//            return answers.get(0);
        }
        return bestAnswer;
    }

    /**
     * 计算推荐分值和最终分值
     * @param answer
     * @param keywords
     * @return
     */
    private Double calculateFinalScore(AnswerDto answer,List<Keyword> keywords) {
        //最终分 = 认可度系数 * 认可度 + 推荐分系数 * 推荐分值 + 关键词匹配系数 * 关键词匹配度
        //对应的系数从数据库推荐算法配置表获取
        CalculateConfig config = calculateConfigMapper.selectOne(new LambdaQueryWrapper<CalculateConfig>().eq(CalculateConfig::getType, 1));
        Double recommendScoreCoe = config.getRecommendScoreCoe();
        Double acceptanceScoreCoe = config.getAcceptanceScoreCoe();
        Double keywordScoreCoe = config.getKeywordScoreCoe();
        Double finalScore = 0.0;
        if (answer == null){
            return null;
        }
        Double recommendScore = answer.getRecommendScore();
        Integer acceptanceCount = answer.getAcceptanceCount();
        finalScore = recommendScoreCoe * recommendScore + acceptanceScoreCoe * acceptanceCount;
        //一个回答有多个关键词，多个关键词对应多个关键词权重
        List<Keyword> answerKeywords = answer.getKeywords();
        for (Keyword answerKeyword : answerKeywords) {
            for (Keyword keyword : keywords) {
                if (answerKeyword.getId() == keyword.getId()) {
                    finalScore += keywordScoreCoe * keyword.getWeight();
                }
            }
        }
        return finalScore;
    }

    /**
     * 根据游戏计算推荐分
     * @param gameId
     * @return
     */
    public Double calculateRecommendScore(Long gameId) {
        //推荐分受游戏推荐指数影响
        if (gameId != null){
            Game game = gameService.getById(gameId);
            if (game != null){
                if (game.getIsTop()){
                    return 100.0;
                }else {
                    //todo 改为BigDecimal类型进行计算
                    double score = 50.0 + game.getViews() / 10000.0 + game.getStars() / 10000.0;
                    if (score > 100.0){
                        return 99.0;
                    }
                    return score;
                }
            }
        }
        return 50.0;
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
    public Answer retry(Long answerId,List<Answer> excludeAnswers,String question) {
        //提高answer的认可度并重新计算最终分值
        Answer answer = answerService.getById(answerId);
        if (answer != null) {
            if (answer.getAcceptanceCount() <= 0){
                //设定为否定状态
                answer.setStatus(false);
            }else {
                //认可度减一
                answer.setAcceptanceCount(answer.getAcceptanceCount() - 1);
            }
            answerService.updateById(answer);
        }
        return questAI(question,excludeAnswers);
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

    private Answer initAnswer(String result,Boolean isTongyi,Long gameId){
        Answer answer = new Answer();
        answer.setContent(result);
        answer.setRecommendScore(50.0);
        answer.setFinalScore(50.0);
        answer.setAcceptanceCount(1);
        answer.setGameId(gameId);
        answer.setStatus(true);
        answer.setIsTongyi(isTongyi);
        answer.setCreateTime(LocalDateTime.now());
        return answer;
    }


}
