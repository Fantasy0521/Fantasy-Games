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
public class Keyword implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关键词内容
     */
    private String content;

    /**
     * 关键词出现次数
     */
    private Integer keywordCount;

    /**
     * 关键词权重
     */
    private Double weight;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
