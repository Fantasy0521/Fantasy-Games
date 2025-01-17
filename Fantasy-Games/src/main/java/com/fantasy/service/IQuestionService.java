package com.fantasy.service;

import com.fantasy.entity.Question;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fantasy.model.vo.QuestionVo;
import com.github.pagehelper.PageInfo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Fantasy0521
 * @since 2025-01-09
 */
public interface IQuestionService extends IService<Question> {

    PageInfo<QuestionVo> getAllQuestionsByPage(Integer pageNum, Integer pageSize);
}
