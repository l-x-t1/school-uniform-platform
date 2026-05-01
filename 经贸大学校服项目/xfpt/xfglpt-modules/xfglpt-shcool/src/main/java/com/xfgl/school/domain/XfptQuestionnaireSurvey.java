package com.xfgl.school.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.experimental.Accessors;
import lombok.*;
import com.xfgl.common.core.annotation.Excel;
import com.xfgl.common.core.web.domain.BaseEntity;

import java.util.List;

/**
 * 问卷调查库对象 xfpt_questionnaire_survey
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
@TableName("xfpt_questionnaire_survey")
@Schema(description = "问卷调查库")
public class XfptQuestionnaireSurvey extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "$column.columnComment")
    private Long id;

    /**
     * 调查内容
     */
    @Excel(name = "调查内容")
    @Schema(description = "调查内容")
    @TableField("survey_content")
    private String surveyContent;

    /**
     * 调查结果
     */
    @Excel(name = "调查结果")
    @Schema(description = "调查结果")
    @TableField("survey_result")
    private String surveyResult;

    /**
     * 0单选  1多选 2文本
     */
    @Schema(description = "作答方式")
    private String answeringMethod;

    /**
     * 关联问卷配置表主键id
     */
    private Long questionConfigId;

    @TableField(exist = false)
    private List<XfptQuestionnaireResult> xfptQuestionnaireResults;

    @TableField(exist = false)
    private int amount;

    private String isRequired;

    /**
     * 说明
     */
    private String sm;


}
