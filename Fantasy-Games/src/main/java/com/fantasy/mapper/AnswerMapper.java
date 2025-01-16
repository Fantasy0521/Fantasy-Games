package com.fantasy.mapper;

import com.fantasy.entity.Answer;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fantasy.entity.Keyword;
import com.fantasy.model.dto.AnswerDto;
import org.apache.ibatis.annotations.Mapper;

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
public interface AnswerMapper extends BaseMapper<Answer> {

    List<AnswerDto> getAnswersByKeyWords(List<Keyword> keywords, List<Answer> excludeAnswers);

    List<Answer> getAnswerByQuestionId(Long id, List<Answer> excludeAnswers);

}
