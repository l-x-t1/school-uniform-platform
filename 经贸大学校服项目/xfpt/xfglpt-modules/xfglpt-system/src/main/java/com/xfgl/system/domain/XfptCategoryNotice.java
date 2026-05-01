package com.xfgl.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.experimental.Accessors;
import lombok.*;
import com.xfgl.common.core.annotation.Excel;
import com.xfgl.common.core.web.domain.TreeEntity;

/**
 * 栏目分类对象 xfpt_category_notice
 *
 * @author yinet
 * @date 2025-05-06
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("xfpt_category_notice")
@Schema(description = "栏目分类")
public class XfptCategoryNotice extends TreeEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 分类名称
     */
    @Excel(name = "分类名称")
    @Schema(description = "分类名称")
    private String name;

    /**
     * 0关闭 1开启
     */
    @Excel(name = "0关闭 1开启")
    @Schema(description = "0关闭 1开启")
    private String status;

    /**
     * 排序
     */
    @Excel(name = "排序")
    @Schema(description = "排序")
    private Long sort;

    /**
     * 排序
     */
    @Excel(name = "分类标识")
    @Schema(description = "分类标识")
    private String bs;

    /**
     * 是否上传封面图
     */
    private String isCoverImage;

}
