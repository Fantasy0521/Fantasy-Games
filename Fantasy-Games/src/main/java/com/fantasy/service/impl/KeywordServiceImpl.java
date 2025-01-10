package com.fantasy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fantasy.entity.Keyword;
import com.fantasy.entity.Question;
import com.fantasy.mapper.KeywordMapper;
import com.fantasy.mapper.QuestionMapper;
import com.fantasy.service.IKeywordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fantasy.util.StringUtils;
import com.fantasy.util.TFIDFUtil;
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

    @Override
    public List<Keyword> getKeyWordsByQuestContent(String content) {
        if (StringUtils.isEmpty(content)) {
            return null;
        }
        //排除自定义非关键词
        Map<String,Integer> noKeyWords = new HashMap<>();
        List<Keyword> noKeywordList = keywordMapper.getNoKeyWords();
        noKeywordList.forEach(keyword -> {
            noKeyWords.put(keyword.getContent(),-1);
        });
        List<List<String>> docs = new ArrayList<>();
        List<Question> questions = questionMapper.selectList(null);
        for (Question question : questions) {
            docs.add(TFIDFUtil.splitContentRemoveNoKeyWord(question.getContent(),noKeyWords));
        }
        List<String> list = TFIDFUtil.calculateTFIDF(content, docs);
        List<Keyword> keywords = new ArrayList<>();
        Map<String, Double> keyWordsAndTFIDF = TFIDFUtil.getKeyWordsAndTFIDF(content, null);
        keyWordsAndTFIDF.forEach((k, v) -> {
            Keyword keyword = getOne(new LambdaQueryWrapper<Keyword>().eq(Keyword::getContent, k));
            if (keyword == null) {
                keyword = new Keyword();
                keyword.setContent(k);
                keyword.setKeywordCount(0);
                keyword.setCreateTime(LocalDateTime.now());
            }
            //关键词出现次数加1
            keyword.setKeywordCount(keyword.getKeywordCount() + 1);
            //设为最新的权重
            keyword.setWeight(v);
        });
        return keywords;
    }

    /**
     * 通过TF-IDF算法计算关键词，并将关键词转化为Keyword对象，返回一个Map，其中包含三个List，分别表示关键词、新关键词词和新词
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

    @Transactional
    @Override
    public void saveKeyWords(List<Keyword> newKeywordList, Long question_id, Long answer_id) {
        saveBatch(newKeywordList);
        //保存两张关联表信息
        keywordMapper.saveQuestionKeywords(newKeywordList, question_id);
        keywordMapper.saveAnswerKeywords(newKeywordList, answer_id);
    }
}
