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
 * 问卷签名对象 xfpt_questionnaire_signature
 *
 * @author yinet
 * @date 2025-06-04
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("xfpt_questionnaire_signature")
@Schema(description = "问卷签名")
public class XfptQuestionnaireSignature extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "$column.columnComment")
    private Long id;

    /**
     * 用户id
     */
    @Excel(name = "用户id")
    @Schema(description = "用户id")
    private Long userId;

    /**
     * 图片
     */
    @Excel(name = "图片")
    @Schema(description = "图片")
    private String image;

    /**
     * 问卷id
     */
    @Excel(name = "问卷id")
    @Schema(description = "问卷id")
    private Long questionnaireSurveyId;

}
