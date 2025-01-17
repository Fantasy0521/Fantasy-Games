package com.fantasy.service.impl;

import com.fantasy.entity.Question;
import com.fantasy.mapper.QuestionMapper;
import com.fantasy.model.vo.QuestionVo;
import com.fantasy.service.IQuestionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fantasy.util.PageUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
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
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements IQuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public PageInfo<QuestionVo> getAllQuestionsByPage(Integer pageNum, Integer pageSize) {
        //查询问题列表以及对于的回答和关键词列表
        List<QuestionVo> questionList = questionMapper.getQuestionInfoList();
        PageInfo<QuestionVo> list = PageUtil.startPageInfo(questionList, pageNum, pageSize);
        return list;
    }


}
