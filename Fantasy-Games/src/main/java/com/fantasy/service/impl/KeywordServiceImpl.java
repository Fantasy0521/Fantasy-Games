package com.fantasy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fantasy.entity.Game;
import com.fantasy.entity.Keyword;
import com.fantasy.entity.Question;
import com.fantasy.mapper.KeywordMapper;
import com.fantasy.mapper.QuestionMapper;
import com.fantasy.model.Result.PageResult;
import com.fantasy.model.vo.GameInfo;
import com.fantasy.service.IKeywordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fantasy.util.StringUtils;
import com.fantasy.util.TFIDFUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Fantasy0521
 * @since 2025-01-09
 */
@Service
public class KeywordServiceImpl extends ServiceImpl<KeywordMapper, Keyword> implements IKeywordService {

    @Autowired
    private KeywordMapper keywordMapper;

    @Autowired
    private QuestionMapper questionMapper;

    /**
     * 实时通过TF-IDF算法计算关键词，并将关键词转化为Keyword对象，返回一个Map，其中包含三个List，分别表示关键词、新关键词词和新词
     * @param content
     * @return
     */
    @Override
    public Map<String, List<Keyword>> getKeyWordsAndNewWordsByQuestContentRealTime(String content) {
        if (StringUtils.isEmpty(content)) {
            return null;
        }
        //排除自定义非关键词
        //非关键词取数据库权重=-1的keyword
        Map<String,Integer> noKeyWords = new HashMap<>();
        List<Keyword> noKeywordList = keywordMapper.getNoKeyWords();
        noKeywordList.forEach(keyword -> {
            noKeyWords.put(keyword.getContent(),-1);
        });
        Map<String, Integer> wordsMap = TFIDFUtil.splitUniqueContent(content,noKeyWords);
        Map<String, Integer> keyWordsMap = new HashMap<>();
        List<List<String>> docs = new ArrayList<>();
        List<Question> questions = questionMapper.selectList(null);
        for (Question question : questions) {
            docs.add(TFIDFUtil.splitContentRemoveNoKeyWord(question.getContent(),noKeyWords));
        }
        //todo 这里每一次都实时计算一次，后续要优化取缓存数据
        List<String> list = TFIDFUtil.calculateTFIDF(content,docs);
        //数据库中关键词
        List<Keyword> keywords = new ArrayList<>();
        //新的关键词
        List<Keyword> newKeyWords = new ArrayList<>();
        //新词（不一定为非关键词）
        List<Keyword> newWords = new ArrayList<>();
        Map<String, Double> keyWordsAndTFIDF = TFIDFUtil.getKeyWordsAndTFIDF(content, null);
        keyWordsAndTFIDF.forEach((k, v) -> {
            if (noKeyWords.get(k) == null) {
                Keyword keyword = getOne(new LambdaQueryWrapper<Keyword>().eq(Keyword::getContent, k));
                if (keyword == null) {
                    keyword = new Keyword();
                    keyword.setContent(k);
                    keyword.setKeywordCount(0);
                    keyword.setWeight(v);
                    keyword.setCreateTime(LocalDateTime.now());
                    newKeyWords.add(keyword);
                }else {
                    //关键词出现次数加1
                    keyword.setKeywordCount(keyword.getKeywordCount() + 1);
                    //设为最新的权重
                    keyword.setWeight(v);
                    keywords.add(keyword);
                }
                keyWordsMap.put(keyword.getContent(),1);
            }
        });
        //添加新词
        wordsMap.forEach((k,v)->{
            if (noKeyWords.get(k) == null) {
                Integer integer = keyWordsMap.get(k);
                if (integer == null){
                    Keyword keyword = new Keyword();
                    keyword.setContent(k);
                    keyword.setKeywordCount(v);
                    keyword.setWeight(0d);
                    keyword.setCreateTime(LocalDateTime.now());
                    newWords.add(keyword);
                }
            }
        });
        Map<String, List<Keyword>> map = new HashMap<>();
        map.put("keywords",keywords);
        map.put("newKeyWords",newKeyWords);
        map.put("newWords",newWords);
        return map;
    }

    /**
     * 从数据库中获取关键词并通过TF-IDF算法计算关键词权重，并将关键词转化为Keyword对象，返回一个Map，其中包含两个List，分别表示关键词、新关键词词
     * @param content
     * @return
     */
    @Override
    public Map<String, List<Keyword>> getKeyWordsAndNewWordsByQuestContent(String content) {
        if (StringUtils.isEmpty(content)) {
            return null;
        }
        //排除自定义非关键词
        //非关键词取数据库权重=-1的keyword
        Map<String,Integer> noKeyWords = new HashMap<>();
        List<Keyword> noKeywordList = keywordMapper.getNoKeyWords();
        noKeywordList.forEach(keyword -> {
            noKeyWords.put(keyword.getContent(),-1);
        });
        //统计问题中出现的词语和出现次数,不包括停用词
        Map<String, Integer> wordsMap = TFIDFUtil.splitUniqueContent(content,noKeyWords);
        if (wordsMap.isEmpty()) {
            return null;
        }
        List<List<String>> docs = new ArrayList<>();
        List<Question> questions = questionMapper.selectList(null);
        for (Question question : questions) {
            docs.add(TFIDFUtil.splitContentRemoveNoKeyWord(question.getContent(),noKeyWords));
        }
        //todo 这里每一次都实时计算一次，后续要优化取缓存数据
        TFIDFUtil.calculateTFIDF(content,docs);
        //数据库中关键词
        List<Keyword> keywords = new ArrayList<>();
        //新的关键词（不一定为非关键词）
        List<Keyword> newWords = new ArrayList<>();
        //改为从数据库中取出wordsMap中的关键词,排除停用词noKeyWords
        List<Keyword> baseAllKeyWords = list(new LambdaQueryWrapper<Keyword>().in(Keyword::getContent, wordsMap.keySet()).ne(Keyword::getWeight,-1));
        Map<String,Keyword> baseAllKeyWordsMap = new HashMap<>();
        baseAllKeyWords.forEach(keyword -> {
            baseAllKeyWordsMap.put(keyword.getContent(),keyword);
        });
        wordsMap.forEach((word,count)->{
            Keyword keyword = baseAllKeyWordsMap.get(word);
            if (keyword != null){//数据库存在该关键词
                if (keyword.getWeight() > 0){
                    //关键词出现次数加1
                    keyword.setKeywordCount(keyword.getKeywordCount() + count);
                    //从TF-IDF算法缓存数据中取权重
                    Double keyWordWeight = TFIDFUtil.getKeyWordWeight(keyword.getContent());
                    if (keyWordWeight != null){
                        //设为最新的权重
                        keyword.setWeight(keyWordWeight);
                    }
                    keywords.add(keyword);
                }else {
                    //数据库中权重=0(未被确认的关键词),这种先去掉不管
                }
            }else {//新词(不一定为关键词)
                Keyword keyword1 = new Keyword();
                keyword1.setContent(word);
                keyword1.setKeywordCount(count);
                keyword1.setWeight(0d);
                keyword1.setCreateTime(LocalDateTime.now());
                newWords.add(keyword1);
            }
        });
        Map<String, List<Keyword>> map = new HashMap<>();
        map.put("keywords",keywords);
        map.put("newWords",newWords);
        return map;
    }

    @Transactional
    @Override
    public void saveKeyWordsAndSub(List<Keyword> newKeywordList, Long question_id, Long answer_id) {
        saveBatch(newKeywordList);
        saveKeyWordsSub(newKeywordList, question_id, answer_id);
    }

    /**
     * 保存两张关联表信息
     */
    @Transactional
    public void saveKeyWordsSub(List<Keyword> keywordList, Long question_id, Long answer_id) {
        if (keywordList == null || keywordList.isEmpty()){
            return;
        }
        keywordMapper.saveQuestionKeywords(keywordList, question_id);
        keywordMapper.saveAnswerKeywords(keywordList, answer_id);
    }

    @Override
    public PageInfo<Keyword> getAllKeywordsByPage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize, "weight desc, keyword_count desc");
        return new PageInfo<>(list());
    }

    @Transactional
    @Override
    public void deleteKeyword(Long id) {
        keywordMapper.deleteById(id);
        //删除两张关联表信息
        keywordMapper.deleteAnswerKeywords(id);
        keywordMapper.deleteQuestionKeywords(id);
    }
}
