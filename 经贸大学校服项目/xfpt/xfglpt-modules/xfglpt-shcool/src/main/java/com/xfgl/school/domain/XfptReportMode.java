package com.xfgl.school.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.experimental.Accessors;
import lombok.*;
import com.xfgl.common.core.annotation.Excel;
import com.xfgl.common.core.web.domain.BaseEntity;

/**
 * 选用组织确定选购模式会议对象 xfpt_report_mode
 *
 * @author yinet
 * @date 2025-04-23
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("xfpt_report_mode")
@Schema(description = "选用组织确定选购模式会议")
public class XfptReportMode extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键id")
    private Long id;

    /**
     * 会议标题
     */
    @Excel(name = "会议标题")
    @Schema(description = "会议标题")
    private String title;

    /**
     * 选购模式
     */
    @Excel(name = "选购模式")
    @Schema(description = "选购模式")
    private String purchaseMode;

    /**
     * 报备表
     */
    @Excel(name = "报备表")
    @Schema(description = "报备表")
    private String reportForm;


    /**
     * 祖籍id
     */
    @Excel(name = "祖籍id")
    @Schema(description = "祖籍id")
    private String ancestors;

    /**
     * 部门id
     */
    @Excel(name = "部门id")
    @Schema(description = "部门id")
    private Long deptId;
    /**
     * 班子会议id
     */
    @Schema(description = "班子会议id")
    private Long teamId;

}
