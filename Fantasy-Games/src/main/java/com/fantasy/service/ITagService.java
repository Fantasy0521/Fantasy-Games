package com.fantasy.service;

import com.fantasy.entity.Keyword;
import com.fantasy.entity.Tag;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fantasy.model.Result.PageResult;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Fantasy0521
 * @since 2023-03-03
 */
public interface ITagService extends IService<Tag> {

    PageResult<Tag> getAllTagsByPage(Integer pageNum, Integer pageSize);

    List<Tag> getTagsByKeyWords(List<Keyword> keywords);

}
