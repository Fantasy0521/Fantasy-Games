package com.fantasy.mapper;

import com.fantasy.entity.ExceptionLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Fantasy0521
 * @since 2023-03-03
 */
@Mapper
public interface ExceptionLogMapper extends BaseMapper<ExceptionLog> {

    List<ExceptionLog> getExceptionLogListByDate(String startDate, String endDate);

    int deleteExceptionLogById(Long id);
}
