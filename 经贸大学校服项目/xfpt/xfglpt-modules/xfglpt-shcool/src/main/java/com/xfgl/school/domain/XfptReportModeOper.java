package com.xfgl.school.domain;

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
 * 确定选购模式操作记录对象 xfpt_report_mode_oper
 *
 * @author yinet
 * @date 2025-04-24
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("xfpt_report_mode_oper")
@Schema(description = "确定选购模式操作记录")
public class XfptReportModeOper extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "$column.columnComment")
    private Long id;

    /**
     * 操作人
     */
    @Excel(name = "操作人")
    @Schema(description = "操作人")
    private String czr;

    /**
     * IP地址
     */
    @Excel(name = "IP地址")
    @Schema(description = "IP地址")
    private String ip;

    /**
     * 确定选购模式会议id
     */
    @Schema(description = "确定选购模式会议id")
    private String reportModeId;

}
