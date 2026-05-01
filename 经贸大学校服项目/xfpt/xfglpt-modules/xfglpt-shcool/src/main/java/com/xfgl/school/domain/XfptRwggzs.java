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

import java.util.List;

/**
 * 企业入围公告对象 xfpt_rwggzs
 *
 * @author yinet
 * @date 2025-07-22
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("xfpt_rwggzs")
@Schema(description = "企业入围公告")
public class XfptRwggzs extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "$column.columnComment")
    private Long id;

    /**
     * 名称
     */
    @Excel(name = "名称")
    @Schema(description = "名称")
    private String name;

    /**
     * 是否发布
     */
    @Excel(name = "是否发布")
    @Schema(description = "是否发布")
    private String isRelease;

    /**
     * 附件
     */
    @Excel(name = "附件")
    @Schema(description = "附件")
    private String file;

    /**
     * $column.columnComment
     */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    @Schema(description = "$column.columnComment")
    private Long deptId;

    /**
     * $column.columnComment
     */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    @Schema(description = "$column.columnComment")
    private String ancestors;

    private Long teamId;

    /**
     * 供应商列表
     */
    @TableField(exist = false)
    private List<XfptSupplierList> supplierList;

    /**
     * 是否是补充公告 0否 1是
     */
    private String type;

}
