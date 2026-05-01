package com.xfgl.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.experimental.Accessors;
import lombok.*;
import com.xfgl.common.core.annotation.Excel;
import com.xfgl.common.core.web.domain.BaseEntity;

/**
 * 轮播图对象 xfpt_carousel_image
 *
 * @author yinet
 * @date 2025-05-19
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("xfpt_carousel_image")
@Schema(description = "轮播图")
public class XfptCarouselImage extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "$column.columnComment")
    private Long id;

    /**
     * $column.columnComment
     */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    @Schema(description = "$column.columnComment")
    private String name;

    /**
     * $column.columnComment
     */
    @Schema(description = "$column.columnComment")
    private String image;

    /**
     * 类型
     */
    @Excel(name = "类型")
    @Schema(description = "类型")
    private String type;

    /**
     * 排序
     */
    @Excel(name = "排序")
    @Schema(description = "排序")
    private Long sort;

}
