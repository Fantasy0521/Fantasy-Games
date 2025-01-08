package com.fantasy.service;

import com.fantasy.entity.ExceptionLog;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Fantasy0521
 * @since 2023-03-03
 */
public interface IExceptionLogService extends IService<ExceptionLog> {

    @Async // 声明为异步方法,在接口定义是为了防止与impl中方法上的@Transaction注解失效!
    void saveExceptionLog(ExceptionLog log);

    List<ExceptionLog> getExceptionLogListByDate(String startDate, String endDate);

    void deleteExceptionLogById(Long id);
}
