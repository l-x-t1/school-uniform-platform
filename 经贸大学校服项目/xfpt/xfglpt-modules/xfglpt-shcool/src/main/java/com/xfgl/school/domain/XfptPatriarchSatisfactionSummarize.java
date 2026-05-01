package com.xfgl.school.domain;

import java.util.Date;
import java.util.List;

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
 * 选用组织家长满意度总结报告报备与公开对象 xfpt_patriarch_satisfaction_summarize
 *
 * @author yinet
 * @date 2025-04-27
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("xfpt_patriarch_satisfaction_summarize")
@Schema(description = "选用组织家长满意度总结报告报备与公开")
public class XfptPatriarchSatisfactionSummarize extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "$column.columnComment")
    private Long id;

    /**
     * 总结报告名称
     */
    @Excel(name = "总结报告名称")
    @Schema(description = "总结报告名称")
    private String summarizeReportName;

    /**
     * 总结报告类型
     */
    @Excel(name = "总结报告类型")
    @Schema(description = "总结报告类型")
    private String summarizeReportType;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "开始时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm")
    @Schema(description = "开始时间")
    private Date startTime;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "结束时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm")
    @Schema(description = "结束时间")
    private Date endTime;

    /**
     * 问卷调查id
     */
    @Excel(name = "问卷调查id")
    @Schema(description = "问卷调查id")
    private String questionnaireSurveyId;

    /**
     * 调查结果附件路径
     */
    @Excel(name = "调查结果附件路径")
    @Schema(description = "调查结果附件路径")
    private String surveyFileUrl;

    /**
     * 调查结果附件名字
     */
    @Excel(name = "调查结果附件名字")
    @Schema(description = "调查结果附件名字")
    private String surveyFileName;

    /**
     * 状态
     */
    @Excel(name = "状态")
    @Schema(description = "状态")
    private String state;

    /**
     * 部门id
     */
    @Excel(name = "部门id")
    @Schema(description = "部门id")
    private String deptId;

    /**
     * 部门名字
     */
    @Excel(name = "部门名字")
    @Schema(description = "部门名字")
    private String deptName;
    /**
     * 班子会议id
     */
    @Excel(name = "班子会议id")
    @Schema(description = "班子会议id")
    private String teamMeetingId;
    /**
     * 部门祖级
     */
    private String ancestors;
    /**
     * 拟购人数
     */
    @Excel(name = "拟购人数")
    @Schema(description = "拟购人数")
    private Long amount;

    @TableField(exist = false)
    private List<XfptBuyGradeClass> xfptBuyGradeClassList;

    @TableField(exist = false)
    private XfptQuestionnaireConfig xfptQuestionnaireConfig;

    @TableField(exist = false)
    private List<XfptQuestionnaireSurvey> xfptQuestionnaireSurveys;

    /**
     * 投票须知
     */
    @TableField(exist = false)
    private String votingNotice;

    /**
     * 投票名单
     */
    private String names;

    private Integer count;

    /**
     * 已投票人数
     */
    @TableField(exist = false)
    private Integer votedCount;
    /**
     * 投票率
     */
    @TableField(exist = false)
    private double votingRate;

    @TableField(exist = false)
    private List<XfptQuestionnaireSurvey> sonList;
}
