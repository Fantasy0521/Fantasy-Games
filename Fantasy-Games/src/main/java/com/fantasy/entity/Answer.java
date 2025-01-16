package com.fantasy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author Fantasy0521
 * @since 2025-01-09
 */
@Data
public class Answer implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long questionId;

    /**
     * 回答内容
     */
    private String content;

    /**
     * 认可度
     */
    private Integer acceptanceCount;

    /**
     * 推荐分
     */
    private Double recommendScore;

    /**
     * 最终分值
     */
    private Double finalScore;

    /**
     * 游戏库中游戏id
     */
    private Long gameId;

    /**
     * 状态
     */
    private Boolean status;

    /**
     * 是否通义生成
     */
    private Boolean isTongyi;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
