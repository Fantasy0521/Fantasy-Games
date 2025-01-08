package com.fantasy.controller.admin;


import com.fantasy.entity.VisitLog;
import com.fantasy.entity.Visitor;
import com.fantasy.model.Result.Result;
import com.fantasy.service.IVisitLogService;
import com.fantasy.service.IVisitorService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
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
@RequestMapping("/admin")
public class VisitorController {

    @Autowired
    IVisitLogService visitLogService;

    @Autowired
    IVisitorService visitorService;
    
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
     * 分页查询访客列表
     *
     * @param date     按最后访问时间查询
     * @param pageNum  页码
     * @param pageSize 每页个数
     * @return
     */
    @GetMapping("/visitors")
    public Result visitors(@RequestParam(defaultValue = "") String[] date,
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
        PageInfo<Visitor> pageInfo = new PageInfo<>(visitorService.getVisitorListByDate(startDate, endDate));
        return Result.ok("请求成功", pageInfo);
    }

    /**
     * 按id删除访客
     * 按uuid删除Redis缓存
     *
     * @param id   访客id
     * @param uuid 访客uuid
     * @return
     */
    @DeleteMapping("/visitor")
    public Result delete(@RequestParam Long id, @RequestParam String uuid) {
        visitorService.deleteVisitor(id, uuid);
        return Result.ok("删除成功");
    }
    
}

