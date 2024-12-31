package com.fantasy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author Fantasy0521
 * @since 2024-12-31
 */
@Data
@TableName("game")
public class Game implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
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

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
