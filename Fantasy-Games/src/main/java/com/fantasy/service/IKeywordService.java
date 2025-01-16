package com.fantasy.service;

import com.fantasy.entity.Keyword;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fantasy.model.Result.PageResult;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Fantasy0521
 * @since 2025-01-09
 */
public interface IKeywordService extends IService<Keyword> {

    Map<String,List<Keyword>> getKeyWordsAndNewWordsByQuestContent(String content);
    Map<String,List<Keyword>> getKeyWordsAndNewWordsByQuestContentRealTime(String content);

    void saveKeyWordsAndSub(List<Keyword> newKeywordList, Long question_id, Long answer_id);
    void saveKeyWordsSub(List<Keyword> newKeywordList, Long question_id, Long answer_id);

    PageInfo<Keyword> getAllKeywordsByPage(Integer pageNum, Integer pageSize);

    void deleteKeyword(Long id);
}
