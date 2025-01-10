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
public class Question implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 首次提问者
     */
    private Long userId;

    /**
     * 问题内容
     */
    private String content;

    /**
     * 问题出现次数
     */
    private Integer questCount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
