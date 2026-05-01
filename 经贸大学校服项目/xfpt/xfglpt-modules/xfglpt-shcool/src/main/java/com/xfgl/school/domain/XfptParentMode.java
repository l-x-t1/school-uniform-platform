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
 * 选用组织组织家长投票确定企业模式(二)对象 xfpt_parent_mode
 *
 * @author yinet
 * @date 2025-05-09
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("xfpt_parent_mode")
@Schema(description = "选用组织组织家长投票确定企业模式(二)")
public class XfptParentMode extends BaseEntity {
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
     * 是否提样衣
     */
    @Excel(name = "是否提样衣")
    @Schema(description = "是否提样衣")
    private String isSamplesPic;

    /**
     * $column.columnComment
     */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    @Schema(description = "$column.columnComment")
    private String samplesPic;

    /**
     * 其他文件
     */
    @Excel(name = "其他文件")
    @Schema(description = "其他文件")
    private String otherFile;

    /**
     * 公告文件
     */
    @Excel(name = "公告文件")
    @Schema(description = "公告文件")
    private String noticeFile;

    /**
     * 问卷id
     */
    @Excel(name = "问卷id")
    @Schema(description = "问卷id")
    private Long quesId;

    /**
     * 报名截止时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "报名截止时间", width = 30, dateFormat = "yyyy-MM-dd")
    @Schema(description = "报名截止时间")
    private Date bmjzTime;

    /**
     * 是否发布 0未发布  1发布 2撤回
     */
    @Excel(name = "是否发布 0未发布  1发布 2撤回")
    @Schema(description = "是否发布 0未发布  1发布 2撤回")
    private String isRelease;

    /**
     * 招标文件
     */
    @Excel(name = "招标文件")
    @Schema(description = "招标文件")
    private String inviteFile;

    /**
     * 班子id
     */
    @Excel(name = "班子id")
    @Schema(description = "班子id")
    private Long teamId;

    /**
     * $column.columnComment
     */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    @Schema(description = "$column.columnComment")
    private Long deptId;

    /**
     * $column.columnComment
     */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    @Schema(description = "$column.columnComment")
    private String ancestors;

    /**
     * 问卷开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "问卷开始时间", width = 30, dateFormat = "yyyy-MM-dd")
    @Schema(description = "问卷开始时间")
    private Date startTime;

    /**
     * 问卷结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "问卷结束时间", width = 30, dateFormat = "yyyy-MM-dd")
    @Schema(description = "问卷结束时间")
    private Date endTime;

}
