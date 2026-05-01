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
 * 评定小组确定供应商对象 xfpt_pdxz_sure_supplier
 *
 * @author yinet
 * @date 2025-05-23
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("xfpt_pdxz_sure_supplier")
@Schema(description = "评定小组确定供应商")
public class XfptPdxzSureSupplier extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "$column.columnComment")
    private Long id;

    /**
     * 班子会议id
     */
    @Excel(name = "班子会议id")
    @Schema(description = "班子会议id")
    private Long teamId;

    /**
     * $column.columnComment
     */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    @Schema(description = "$column.columnComment")
    private String ancestors;

    /**
     * $column.columnComment
     */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    @Schema(description = "$column.columnComment")
    private Long deptId;

    /**
     * 标题
     */
    @Excel(name = "标题")
    @Schema(description = "标题")
    private String title;

    /**
     * 评定小组名单
     */
    @Excel(name = "评定小组名单")
    @Schema(description = "评定小组名单")
    private String userFile;

    /**
     * 问卷id
     */
    @Excel(name = "问卷id")
    @Schema(description = "问卷id")
    private String questId;

    @Excel(name = "其他文件")
    @Schema(description = "其他文件")
    private String otherFile;

    @Excel(name = "类型")
    @Schema(description = "类型")
    private String type;

    /**
     *导入人名单
     */
    private String names;

    /**
     * 投票人数
     */
    private Integer count;

    /**
     * 参与人数
     */
    @TableField(exist = false)
    private Integer votedCount;

    /**
     * 投票率
     */
    @TableField(exist = false)
    private double votingRate;

}
