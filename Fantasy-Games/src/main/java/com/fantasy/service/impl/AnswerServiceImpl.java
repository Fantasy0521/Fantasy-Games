package com.fantasy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fantasy.entity.Answer;
import com.fantasy.entity.Keyword;
import com.fantasy.mapper.AnswerMapper;
import com.fantasy.model.dto.AnswerDto;
import com.fantasy.service.IAnswerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Fantasy0521
 * @since 2025-01-09
 */
@Service
public class AnswerServiceImpl extends ServiceImpl<AnswerMapper, Answer> implements IAnswerService {

    @Autowired
    private AnswerMapper answerMapper;

    @Override
    public Answer getAnswerByQuestionId(Long id,List<Answer> excludeAnswers) {
        List<Answer> list = answerMapper.getAnswerByQuestionId(id,excludeAnswers);
        //计算最终分值
        if (!list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public List<AnswerDto> getAnswersByKeyWords(List<Keyword> keywords, List<Answer> excludeAnswers) {
        return answerMapper.getAnswersByKeyWords(keywords,excludeAnswers);
    }

}
