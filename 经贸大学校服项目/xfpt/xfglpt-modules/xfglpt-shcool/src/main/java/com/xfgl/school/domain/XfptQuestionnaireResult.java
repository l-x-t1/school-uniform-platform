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
 * 问卷调查结果对象 xfpt_questionnaire_result
 *
 * @author yinet
 * @date 2025-05-07
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("xfpt_questionnaire_result")
@Schema(description = "问卷调查结果")
public class XfptQuestionnaireResult extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "$column.columnComment")
    private Long id;

    /**
     * 问卷调查id
     */
    @Excel(name = "问卷调查id")
    @Schema(description = "问卷调查id")
    private String questionnaireSurveyId;

    /**
     * 问卷项id
     */
    @Excel(name = "问卷项id")
    @Schema(description = "问卷项id")
    private String surveyId;

    /**
     * 问卷结果
     */
    @Excel(name = "问卷结果")
    @Schema(description = "问卷结果")
    private String questionnaireResult;

    /**
     * 家长姓名
     */
    @Excel(name = "家长姓名")
    @Schema(description = "家长姓名")
    private String patriarchName;

    /**
     * 学生姓名
     */
    @Excel(name = "学生姓名")
    @Schema(description = "学生姓名")
    private String studentName;

    //百分比
    @TableField(exist = false)
    private String percentage;

    @TableField(exist = false)
    private int amount;


    @Excel(name = "ipAddress")
    @Schema(description = "ipAddress")
    private String ipAddress;

    /**
     * 签名
     */
    private String image;

    private String name;
}
