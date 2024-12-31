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
@TableName("game_image")
public class GameImage implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 游戏名称
     */
    private String name;

    /**
     * 图片地址
     */
    private String url;

    /**
     * 顺序
     */
    private Integer rn;

    /**
     * 游戏id
     */
    private Long gameId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
