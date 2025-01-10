package com.fantasy.service;

import com.fantasy.entity.Keyword;
import com.baomidou.mybatisplus.extension.service.IService;

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

    List<Keyword> getKeyWordsByQuestContent(String content);
    Map<String,List<Keyword>> getKeyWordsAndNewWordsByQuestContent(String content);

    void saveKeyWords(List<Keyword> newKeywordList, Long question_id, Long answer_id);
}
