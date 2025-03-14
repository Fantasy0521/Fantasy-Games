package com.fantasy.controller.admin;


import com.fantasy.entity.VisitLog;
import com.fantasy.model.Result.Result;
import com.fantasy.service.IVisitLogService;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Fantasy0521
 * @since 2023-03-03
 */
@RestController
@RequestMapping("admin/fantasy/visitLog")
public class VisitLogController {

    @Autowired
    IVisitLogService visitLogService;

    /**
     * 分页查询访问日志列表
     *
     * @param uuid     按访客标识码模糊查询
     * @param date     按访问时间查询
     * @param pageNum  页码
     * @param pageSize 每页个数
     * @return
     */
    @GetMapping("/visitLogs")
    public Result visitLogs(@RequestParam(defaultValue = "") String uuid,
                            @RequestParam(defaultValue = "") String[] date,
                            @RequestParam(defaultValue = "1") Integer pageNum,
                            @RequestParam(defaultValue = "10") Integer pageSize) {
        String startDate = null;
        String endDate = null;
        if (date.length == 2) {
            startDate = date[0];
            endDate = date[1];
        }
        String orderBy = "create_time desc";
        PageHelper.startPage(pageNum, pageSize, orderBy);
        PageInfo<VisitLog> pageInfo = new PageInfo<>(visitLogService.getVisitLogListByUUIDAndDate(StringUtils.trim(uuid), startDate, endDate));
        return Result.ok("请求成功", pageInfo);
    }

    /**
     * 按id删除访问日志
     *
     * @param id 日志id
     * @return
     */
    @DeleteMapping("/visitLog")
    public Result delete(@RequestParam Long id) {
        visitLogService.deleteVisitLogById(id);
        return Result.ok("删除成功");
    }

}

