package com.fantasy.service;

import com.fantasy.entity.Answer;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fantasy.entity.Keyword;

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

    Answer getAnswerByQuestionId(Long id);

    List<Answer> getAnswersByKeyWords(List<Keyword> keywords);
}
