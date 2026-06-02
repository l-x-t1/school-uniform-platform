package com.xfgl.school.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.xfgl.common.core.annotation.Excel;
import com.xfgl.common.core.web.domain.TreeEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

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
@Schema(description = "学校班级")
public class Classes extends TreeEntity {

    @TableId(type = IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
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
}
