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
 * 利旧回收对象 xfpt_ljhs
 *
 * @author yinet
 * @date 2025-09-15
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("xfpt_ljhs")
@Schema(description = "利旧回收")
public class XfptLjhs extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    @Schema(description = "$column.columnComment")
    private Long id;

    /**
     * 姓名
     */
    @Excel(name = "姓名")
    @Schema(description = "姓名")
    private String name;

    /**
     * 学校id
     */
    @Schema(description = "学校id")
    private Long schoolId;

    /**
     * 班级名称
     */
    @Excel(name = "班级名称")
    @Schema(description = "班级名称")
    private String className;

    /**
     * 校服图片
     */
    @Schema(description = "校服图片")
    private String images;

    /**
     * 学校名称
     */
    @Excel(name = "学校名称")
    @Schema(description = "学校名称")
    private String schoolName;

    /**
     * 审核状态
     */
    @Excel(name = "审核状态")
    @Schema(description = "审核状态")
    private String status;

    /**
     * 其他文件
     */
    @Schema(description = "其他文件")
    private String otherFile;

    @Schema(description = "石i民用户id")
    private String simId;

}
