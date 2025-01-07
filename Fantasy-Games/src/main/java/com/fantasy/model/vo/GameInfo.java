package com.fantasy.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fantasy.entity.Category;
import com.fantasy.entity.GameImage;
import com.fantasy.entity.Tag;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * GameVo
 */
@Data
public class GameInfo {

    private Long id;

    /**
     * 游戏名称
     */
    private String name;

    /**
     * 开发商
     */
    private String developers;

    /**
     * 发行商
     */
    private String publisher;

    /**
     * 发行日期
     */
    private LocalDateTime publishDate;

    /**
     * 发行日期
     */
    private String publishDateString;

    /**
     * 系列
     */
    private String series;

    /**
     * 官方地址
     */
    private String officialUrl;

    /**
     * 下载地址
     */
    private String downloadUrl;

    /**
     * 游戏首图，用于展示
     */
    private String firstPicture;

    /**
     * 描述
     */
    private String description;

    /**
     * 公开或私密
     */
    private Boolean isPublished;

    /**
     * 推荐开关
     */
    private Boolean isRecommend;

    /**
     * 赞赏开关
     */
    private Boolean isAppreciation;

    /**
     * 评论开关
     */
    private Boolean isCommentEnabled;

    /**
     * 浏览次数
     */
    private Integer views;

    /**
     * 收藏次数
     */
    private Integer stars;

    /**
     * 游戏分类
     */
    private Long categoryId;

    /**
     * 是否置顶
     */
    private Boolean isTop;

    /**
     * 提取码
     */
    private String password;

    // LocalDateTime change to Date
    private Date createTime;//创建时间

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


    //扩展字段
    /**
     * 游戏分类
     */
    private Category category;

    /**
     * 标签
     */
    private List<Tag> tags = new ArrayList<>();

    /**
     * 游戏图片
     */
    private List<GameImage> gameImages = new ArrayList<>();


}
