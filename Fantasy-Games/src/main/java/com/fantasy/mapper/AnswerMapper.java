package com.fantasy.mapper;

import com.fantasy.entity.Answer;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fantasy.entity.Keyword;
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

    List<Answer> getAnswersByKeyWords(List<Keyword> keywords,Long answerId);

}
