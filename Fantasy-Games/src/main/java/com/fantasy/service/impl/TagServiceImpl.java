package com.fantasy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fantasy.entity.Keyword;
import com.fantasy.entity.Tag;
import com.fantasy.entity.Tag;
import com.fantasy.mapper.TagMapper;
import com.fantasy.model.Result.PageResult;
import com.fantasy.service.ITagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Fantasy0521
 * @since 2023-03-03
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements ITagService {

    @Override
    public PageResult<Tag> getAllTagsByPage(Integer pageNum, Integer pageSize) {
        //1 构建分页对象
        Page<Tag> pageInfo = new Page<>(pageNum, pageSize);

        //2 创建LambdaQueryWrapper
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();

        //4 开始分页查询
        Page<Tag> tagPage = this.page(pageInfo, queryWrapper);


        //6 封装为前端规定的PageResult
        // 此处getPages需要加一
        PageResult<Tag> result = new PageResult<>((int) tagPage.getPages() + 1, tagPage.getRecords());

        return result;
    }

    @Override
    public List<Tag> getTagsByKeyWords(List<Keyword> keywords) {
        if (keywords == null) {
            return null;
        }
        List<String> collect = keywords.stream().map(Keyword::getContent).collect(Collectors.toList());
        return this.list(new LambdaQueryWrapper<Tag>().in(Tag::getName, collect));
    }
}
