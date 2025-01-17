package com.fantasy.util;

import com.github.pagehelper.PageInfo;

import java.util.Collections;
import java.util.List;

public class PageUtil {

    /**
     * 手动分页
     *
     * @param list
     * @param pageNum
     * @param pageSize
     * @return
     */
    public static List startPage(List list, Integer pageNum, Integer pageSize) {
        if (list == null) {
            return Collections.emptyList();
        }
        if (list.isEmpty()) {
            return Collections.emptyList();
        }
        //记录总数
        Integer count = list.size();

        //开始索引
        int fromIndex = (pageNum - 1) * pageSize;
        //结束索引
        int toIndex = pageNum * pageSize;
        if (fromIndex + 1 > count) {
            return Collections.emptyList();
        }
        if (pageNum * pageSize > count) {
            toIndex = count;
        }
        return list.subList(fromIndex, toIndex);
    }

    /**
     * 手动分页并返回分页信息
     *
     * @param list
     * @param pageNum
     * @param pageSize
     * @return
     */
    public static <T> PageInfo<T> startPageInfo(List<T> list, Integer pageNum, Integer pageSize) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        //记录总数
        Integer count = list.size();

        //开始索引
        int fromIndex = (pageNum - 1) * pageSize;
        //结束索引
        int toIndex = pageNum * pageSize;
        if (fromIndex + 1 > count) {
            return null;
        }
        if (pageNum * pageSize > count) {
            toIndex = count;
        }
        List<T> ts = list.subList(fromIndex, toIndex);

        PageInfo<T> pageInfo = new PageInfo<>();
        pageInfo.setList(ts);
        pageInfo.setTotal(count);
        pageInfo.setPages(count / pageSize + 1);
        return pageInfo;
    }

}
