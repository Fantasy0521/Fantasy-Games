package com.fantasy.service;

import com.fantasy.entity.Answer;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fantasy.entity.Keyword;
import com.fantasy.model.dto.AnswerDto;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Fantasy0521
 * @since 2025-01-09
 */
public interface IAnswerService extends IService<Answer> {

    Answer getAnswerByQuestionId(Long id,List<Answer> excludeAnswers);

    List<AnswerDto> getAnswersByKeyWords(List<Keyword> keywords, List<Answer> excludeAnswers);
}
