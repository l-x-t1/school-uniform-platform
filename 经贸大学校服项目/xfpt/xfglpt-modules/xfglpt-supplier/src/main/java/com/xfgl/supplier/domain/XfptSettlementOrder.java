package com.xfgl.supplier.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
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
 * 结算单对象 xfpt_settlement_order
 *
 * @author yinet
 * @date 2026-05-01
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("xfpt_settlement_order")
@Schema(description = "结算单")
public class XfptSettlementOrder extends BaseEntity
        {
private static final long serialVersionUID = 1L;

    /** 主键 */
        @TableId(type= IdType.AUTO)
    @Schema(description = "主键")
    private Long id;

    /** 结算单号 */
            @Excel(name = "结算单号")
    @Schema(description = "结算单号")
    private String settlementNo;

    /** 学校ID */
            @Excel(name = "学校ID")
    @Schema(description = "学校ID")
    private Long schoolId;

    /** 学校名称快照 */
            @Excel(name = "学校名称快照")
    @Schema(description = "学校名称快照")
    private String schoolName;

    /** 供应商ID */
            @Excel(name = "供应商ID")
    @Schema(description = "供应商ID")
    private Long supplierId;

    /** 供应商名称快照 */
            @Excel(name = "供应商名称快照")
    @Schema(description = "供应商名称快照")
    private String supplierName;

    /** 结算开始时间 */
            @JsonFormat(pattern = "yyyy-MM-dd")
            @Excel(name = "结算开始时间", width = 30, dateFormat = "yyyy-MM-dd")
    @Schema(description = "结算开始时间")
    private Date settlementPeriodStart;

    /** 结算结束时间 */
            @JsonFormat(pattern = "yyyy-MM-dd")
            @Excel(name = "结算结束时间", width = 30, dateFormat = "yyyy-MM-dd")
    @Schema(description = "结算结束时间")
    private Date settlementPeriodEnd;

    /** 订单数 */
            @Excel(name = "订单数")
    @Schema(description = "订单数")
    private Integer orderCount;

    /** 结算金额 */
            @Excel(name = "结算金额")
    @Schema(description = "结算金额")
    private BigDecimal settlementAmount;

    /** 税额 */
            @Excel(name = "税额")
    @Schema(description = "税额")
    private BigDecimal taxAmount;

    /** 已付金额 */
            @Excel(name = "已付金额")
    @Schema(description = "已付金额")
    private BigDecimal paidAmount;

    /** 未付金额 */
            @Excel(name = "未付金额")
    @Schema(description = "未付金额")
    private BigDecimal unpaidAmount;

    /** 状态，0待申请 1待审核 2已通过 3已完成 */
            @Excel(name = "状态，0待申请 1待审核 2已通过 3已完成")
    @Schema(description = "状态，0待申请 1待审核 2已通过 3已完成")
    private String status;

    /** 申请时间 */
            @JsonFormat(pattern = "yyyy-MM-dd")
            @Excel(name = "申请时间", width = 30, dateFormat = "yyyy-MM-dd")
    @Schema(description = "申请时间")
    private Date applyTime;

    /** 审核时间 */
            @JsonFormat(pattern = "yyyy-MM-dd")
            @Excel(name = "审核时间", width = 30, dateFormat = "yyyy-MM-dd")
    @Schema(description = "审核时间")
    private Date auditTime;

    /** 审核人 */
            @Excel(name = "审核人")
    @Schema(description = "审核人")
    private String auditorName;

    /** 审核备注 */
            @Excel(name = "审核备注")
    @Schema(description = "审核备注")
    private String auditRemark;

    /** 当前归属部门/学校 */
            @Excel(name = "当前归属部门/学校")
    @Schema(description = "当前归属部门/学校")
    private Long deptId;

    /** 组织祖级链路 */
            @Excel(name = "组织祖级链路")
    @Schema(description = "组织祖级链路")
    private String ancestors;

        }
