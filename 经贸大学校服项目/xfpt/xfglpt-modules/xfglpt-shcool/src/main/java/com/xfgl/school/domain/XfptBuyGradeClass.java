package com.xfgl.school.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.experimental.Accessors;
import lombok.*;
import com.xfgl.common.core.annotation.Excel;
import com.xfgl.common.core.web.domain.BaseEntity;

import java.util.List;

/**
 * 采购年级对象 xfpt_buy_grade_class
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
@TableName("xfpt_buy_grade_class")
@Schema(description = "采购年级")
public class XfptBuyGradeClass extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    @Schema(description = "$column.columnComment")
    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 年级或班级id
     */
    @Excel(name = "年级或班级id")
    @Schema(description = "年级或班级id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long gradeClassId;

    /**
     * 年级或班级名称
     */
    @Excel(name = "年级或班级名称")
    @Schema(description = "年级或班级名称")
    private String gradeClassName;

    /**
     * 拟购人数
     */
    @Excel(name = "拟购人数")
    @Schema(description = "拟购人数")
    private Long amount;

    /**
     * 区分是那个模块
     */
    @Excel(name = "区分是那个模块")
    @Schema(description = "区分是那个模块")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long category;

    /**
     * 对应模块的主键id
     */
    @Excel(name = "对应模块的主键id")
    @Schema(description = "对应模块的主键id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long pid;

    @TableField(exist = false)
    private List<Long> pidList;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentId;
}
