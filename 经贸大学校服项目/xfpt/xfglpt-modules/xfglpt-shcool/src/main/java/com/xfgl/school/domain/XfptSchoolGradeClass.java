package com.xfgl.school.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.xfgl.common.core.web.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.experimental.Accessors;
import lombok.*;
import com.xfgl.common.core.annotation.Excel;
import com.xfgl.common.core.web.domain.TreeEntity;

import java.util.List;

/**
 * 学校年级班级对象 xfpt_school_grade_class
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
@TableName("xfpt_school_grade_class")
@Schema(description = "学校年级班级")
public class XfptSchoolGradeClass  extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "$column.columnComment")
    private Long id;

    /**
     * 部门id
     */
    @Excel(name = "部门id")
    @Schema(description = "部门id")
    private String deptId;

    /**
     * 部门名称
     */
    @Excel(name = "部门名称")
    @Schema(description = "部门名称")
    private String deptName;

    /**
     * 年级班级名字
     */
    @Excel(name = "年级班级名字")
    @Schema(description = "年级班级名字")
    private String gradeClass;

    /**
     * 班级人数
     */
    @Excel(name = "班级人数")
    @Schema(description = "班级人数")
    private String number;

    /**
     * 启用状态 0启动 1禁用
     */
    @Excel(name = "启用状态 0启动 1禁用")
    @Schema(description = "启用状态 0启动 1禁用")
    private String status;

    @TableField(exist = false)
    private List<Classes> classes;

    /** 父菜单ID */
    private Long parentId;

    @Excel(name = "年份")
    @Schema(description = "年份")
    private String gradeYear;

    /** 祖级列表 */
    private String ancestors;

    /**
     * 采购id
     */
    private Long cgId;
}
