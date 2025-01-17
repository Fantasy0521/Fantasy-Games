package com.fantasy.controller.admin;

import com.fantasy.entity.CalculateConfig;
import com.fantasy.mapper.CalculateConfigMapper;
import com.fantasy.model.Result.Result;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("admin/fantasy/calculate")
public class CalculateAdminController {

    @Autowired
    private CalculateConfigMapper calculateConfigMapper;

    @GetMapping("/calculates")
    public Result getCalculateConfigs() {
        List<CalculateConfig> calculateConfigs = calculateConfigMapper.selectList(null);
        return Result.ok("获取算法配置成功!",new PageInfo<>(calculateConfigs));
    }

    @PutMapping
    public Result updateCalculateConfig(@RequestBody CalculateConfig calculateConfig) {
        calculateConfigMapper.updateById(calculateConfig);
        return Result.ok("更新算法配置成功!");
    }

    
}
