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
 * 采购流程对象 xfpt_purchase_process
 *
 * @author yinet
 * @date 2025-08-06
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("xfpt_purchase_process")
@Schema(description = "采购流程")
public class XfptPurchaseProcess extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 采购流程
     */
    @Schema(description = "采购流程")
    private Long id;

    /**
     * 采购名称
     */
    @Excel(name = "采购名称")
    @Schema(description = "采购名称")
    private String purchaseName;

    /**
     * 采购年度
     */
    @Excel(name = "采购年度")
    @Schema(description = "采购年度")
    private String purchaseYear;

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

    @TableField(exist = false)
    private List<XfptSchoolGradeClass> xfptSchoolGradeClass;


    private String cgTime;

    @TableField(exist = false)
    private Long teamId;


}
