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
 * 财务流水对象 xfpt_finance_flow
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
@TableName("xfpt_finance_flow")
@Schema(description = "财务流水")
public class XfptFinanceFlow extends BaseEntity
        {
private static final long serialVersionUID = 1L;

    /** 主键 */
        @TableId(type= IdType.AUTO)
    @Schema(description = "主键")
    private Long id;

    /** 流水号 */
            @Excel(name = "流水号")
    @Schema(description = "流水号")
    private String flowNo;

    /** 业务类型（1支付 2退款 3结算 4提现） */
            @Excel(name = "业务类型", readConverterExp = "1=支付,2=退款,3=结算,4=提现")
    @Schema(description = "业务类型（1支付 2退款 3结算 4提现）")
    private String businessType;

    /** 订单ID */
            @Excel(name = "订单ID")
    @Schema(description = "订单ID")
    private Long orderId;

    /** 售后ID */
            @Excel(name = "售后ID")
    @Schema(description = "售后ID")
    private Long afterSaleId;

    /** 学校ID */
            @Excel(name = "学校ID")
    @Schema(description = "学校ID")
    private Long schoolId;

    /** 供应商ID */
            @Excel(name = "供应商ID")
    @Schema(description = "供应商ID")
    private Long supplierId;

    /** 用户ID */
            @Excel(name = "用户ID")
    @Schema(description = "用户ID")
    private Long userId;

    /** 金额 */
            @Excel(name = "金额")
    @Schema(description = "金额")
    private BigDecimal amount;

    /** 支付渠道 */
            @Excel(name = "支付渠道")
    @Schema(description = "支付渠道")
    private String payChannel;

    /** 交易流水号 */
            @Excel(name = "交易流水号")
    @Schema(description = "交易流水号")
    private String tradeNo;

    /** 状态（0待处理 1成功 2失败 3关闭） */
            @Excel(name = "状态", readConverterExp = "0=待处理,1=成功,2=失败,3=关闭")
    @Schema(description = "状态（0待处理 1成功 2失败 3关闭）")
    private String status;

    /** 交易完成时间 */
            @JsonFormat(pattern = "yyyy-MM-dd")
            @Excel(name = "交易完成时间", width = 30, dateFormat = "yyyy-MM-dd")
    @Schema(description = "交易完成时间")
    private Date tradeTime;

    /** 部门ID */
            @Excel(name = "部门ID")
    @Schema(description = "部门ID")
    private Long deptId;

    /** 祖级列表 */
            @Excel(name = "祖级列表")
    @Schema(description = "祖级列表")
    private String ancestors;

        }
