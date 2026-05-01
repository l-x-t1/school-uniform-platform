package com.xfgl.school.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.xfgl.common.core.annotation.Excel;
import com.xfgl.common.core.web.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 学校与供应商采购合同关联对象 xfpt_supplice_contract
 *
 * @author yinet
 * @date 2025-04-22
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("xfpt_supplice_contract")
@Schema(description = "学校与供应商采购合同关联")
public class XfptSuppliceContract extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "$column.columnComment")
    private Long id;

    /**
     * 学校部门id
     */
    @Excel(name = "学校部门id")
    @Schema(description = "学校部门id")
    private String deptId;

    /**
     * 学校名字
     */
    @Excel(name = "学校名字")
    @Schema(description = "学校名字")
    private String deptName;

    /**
     * 厂商id
     */
    @Excel(name = "厂商id")
    @Schema(description = "厂商id")
    private String supplierId;

    /**
     * 厂商名字
     */
    @Excel(name = "厂商名字")
    @Schema(description = "厂商名字")
    private String supplierName;

    /**
     * 厂商联系电话
     */
    @Excel(name = "厂商联系电话")
    @Schema(description = "厂商联系电话")
    private String supplierPhone;

    /**
     * 班子会议id
     */
    @Excel(name = "班子会议id")
    @Schema(description = "班子会议id")
    private String teamMeetingId;
    /**
     * 部门祖级
     */
    private String ancestors;

    /**
     * 负责人
     */
    private String fzr;

    /**
     * 负责人电话
     */
    private String fzrdh;

    /**
     * 数量
     */
    private Integer count;

    /**
     * 单价
     */
    private Double dj;


    /**
     * 总价
     */
    private Double zj;

    //学生数量
    @TableField(exist = false)
    private String studentNumber;

    @TableField(exist = false)
    private String legalRepresentative;
}
