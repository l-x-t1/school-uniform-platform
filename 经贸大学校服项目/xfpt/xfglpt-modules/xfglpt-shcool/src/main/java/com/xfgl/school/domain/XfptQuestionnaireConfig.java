package com.xfgl.school.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.experimental.Accessors;
import lombok.*;
import com.xfgl.common.core.annotation.Excel;
import com.xfgl.common.core.web.domain.BaseEntity;

import java.util.Date;
import java.util.List;

/**
 * 问卷调查主配置对象 xfpt_questionnaire_config
 *
 * @author wxm
 * @date 2025-04-27
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("xfpt_questionnaire_config")
@Schema(description = "问卷调查主配置")
public class XfptQuestionnaireConfig extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "$column.columnComment")
    private Long id;

    /**
     * 问卷标题
     */
    @Excel(name = "问卷标题")
    @Schema(description = "问卷标题")
    private String title;

    /**
     * 启用状态 默认1
     */
    @Excel(name = "启用状态 默认1")
    @Schema(description = "启用状态 默认1")
    private Long useStatus;


    /**
     * 问卷类型：0普通问卷；1：模板问卷; 2：班子会议内部流程
     */
    private String category;
    /**
     * 发布状态0未发布
     */
    private Long fbStatus;

    /**
     * 问卷开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date beginTime;

    /**
     * 问卷结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;


    /**
     * 区分是那个模块
     */
    private String type;
    /**
     * 部门id
     */
    private Long deptId;
    /**
     * 部门祖级
     */
    private String ancestors;

    @TableField(exist = false)
    private List<XfptQuestionnaireSurvey> sonList;

    @TableField(exist = false)
    private String state;

    //单选
    @TableField(exist = false)
    private int multipleChoice = 0;
    //多选
    @TableField(exist = false)
    private int multipleResponse = 0;
    //文本
    @TableField(exist = false)
    private int text = 0;

    private String wjStatus;

    /**
     * 投票信息
     */
    private String votingDescription;

    /**
     * 投票须知
     */
    private String votingNotice;

    /**
     * 名单
     */
    private String names;

    /**
     * 分类 0教育局发布  1学校发布
     */
    private String lcCategory;

    @TableField(exist = false)
    private List<String> deptIds;

    @TableField(exist = false)
    private Long viewerDeptId;

    @TableField(exist = false)
    private String viewerAncestors;

    @TableField(exist = false)
    private String viewerIsSchool;

}
