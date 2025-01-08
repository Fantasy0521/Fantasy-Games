package com.fantasy.mapper;

import com.fantasy.entity.Visitor;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fantasy.model.dto.VisitLogUuidTime;
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
public interface VisitorMapper extends BaseMapper<Visitor> {

    List<Visitor> getVisitorListByDate(String startDate, String endDate);

    List<String> getNewVisitorIpSourceByYesterday();


    int hasUUID(String uuid);

    void updatePVAndLastTimeByUUID(VisitLogUuidTime dto);

    int deleteVisitorById(Long id);

}
