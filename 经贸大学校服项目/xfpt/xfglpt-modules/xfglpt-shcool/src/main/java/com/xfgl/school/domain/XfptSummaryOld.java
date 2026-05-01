package com.xfgl.school.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
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
 * 采购历史数据对象 xfpt_summary_old
 *
 * @author yinet
 * @date 2025-06-03
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("xfpt_summary_old")
@Schema(description = "采购历史数据")
public class XfptSummaryOld extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "$column.columnComment")
    private Long id;

    /**
     * 标题
     */
    @Excel(name = "标题")
    @Schema(description = "标题")
    private String title;

    /**
     * 采购金额
     */
    @Excel(name = "采购金额")
    @Schema(description = "采购金额")
    private Long money;

    /**
     * 采购公告
     */
    @Schema(description = "采购公告")
    private String inviteFile;

    /**
     * 成交公告
     */
    @Schema(description = "成交公告")
    private String winBidFile;

    /**
     * 企业
     */
    @Excel(name = "企业")
    @Schema(description = "企业")
    private String enterprise;

    /**
     * 采购数量
     */
    @Excel(name = "采购数量")
    @Schema(description = "采购数量")
    private Long count;

    /**
     * 校服样式
     */
    @Schema(description = "校服样式")
    private String schoolUniformFile;

    /**
     * 学校名称
     */
    @Excel(name = "学校名称")
    @Schema(description = "学校名称")
    private String schoolName;

    /**
     * $column.columnComment
     */
    @Schema(description = "$column.columnComment")
    private Long deptId;

    /**
     * $column.columnComment
     */
    @Schema(description = "$column.columnComment")
    private String ancestors;

    /**
     * 采购日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "采购日期", width = 30, dateFormat = "yyyy-MM-dd")
    @Schema(description = "采购日期")
    private Date buyTime;

    /**
     * 其他附件
     */
    private String otherFile;

}
