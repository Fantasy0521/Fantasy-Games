package com.fantasy.mapper;

import com.fantasy.entity.Question;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fantasy.model.vo.QuestionVo;
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
public interface QuestionMapper extends BaseMapper<Question> {

    List<QuestionVo> getQuestionInfoList();

}
