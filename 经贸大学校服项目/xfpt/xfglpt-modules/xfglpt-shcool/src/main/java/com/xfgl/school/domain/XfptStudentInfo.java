package com.xfgl.school.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xfgl.system.api.domain.SysDept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.experimental.Accessors;
import lombok.*;
import com.xfgl.common.core.annotation.Excel;
import com.xfgl.common.core.web.domain.BaseEntity;

import java.util.List;

/**
 * 家长绑定学生信息对象 xfpt_student_info
 *
 * @author yinet
 * @date 2025-05-13
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("xfpt_student_info")
@Schema(description = "家长绑定学生信息")
public class XfptStudentInfo extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "$column.columnComment")
    private Long id;

    /**
     * 班级年级
     */
    @Excel(name = "班级年级")
    @Schema(description = "班级年级")
    private String classId;

    /**
     * 学校
     */
    @Excel(name = "学校")
    @Schema(description = "学校")
    private String shcoolId;

    /**
     * 姓名
     */
    @Excel(name = "姓名")
    @Schema(description = "姓名")
    private String name;

    /**
     * 性别
     */
    @Excel(name = "性别")
    @Schema(description = "性别")
    private String sex;

    /**
     * 出生年月日
     */
    @Excel(name = "出生年月日")
    @Schema(description = "出生年月日")
    private String birthday;

    /**
     * 石i民家长关联id
     */
    @Excel(name = "石i民家长关联id")
    @Schema(description = "石i民家长关联id")
    private String simId;

    @TableField(exist = false)
    private SysDept sysDepts;

    @TableField(exist = false)
    private XfptSchoolGradeClass xfptSchoolGradeClasses;

    @Excel(name = "身份证号")
    @Schema(description = "身份证号")
    private String idNumber;

    private String grade;

    @TableField(exist = false)
    private String phone;
}
