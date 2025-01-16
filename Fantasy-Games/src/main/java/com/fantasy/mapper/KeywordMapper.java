package com.fantasy.mapper;

import com.fantasy.entity.Keyword;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Fantasy0521
 * @since 2025-01-09
 */
@Mapper
public interface KeywordMapper extends BaseMapper<Keyword> {

    void saveQuestionKeywords(@Param("keywords") List<Keyword> newKeywordList,@Param("questionId")  Long questionId);

    void saveAnswerKeywords(@Param("keywords") List<Keyword> newKeywordList,@Param("answerId")  Long answerId);

    List<Keyword> getNoKeyWords();

    void deleteAnswerKeywords(Long id);

    void deleteQuestionKeywords(Long id);
}
