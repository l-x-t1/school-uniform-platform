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
 * 结算明细对象 xfpt_settlement_order_item
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
@TableName("xfpt_settlement_order_item")
@Schema(description = "结算明细")
public class XfptSettlementOrderItem extends BaseEntity
        {
private static final long serialVersionUID = 1L;

    /** 主键 */
        @TableId(type= IdType.AUTO)
    @Schema(description = "主键")
    private Long id;

    /** 结算单ID */
            @Excel(name = "结算单ID")
    @Schema(description = "结算单ID")
    private Long settlementId;

    /** 订单ID */
            @Excel(name = "订单ID")
    @Schema(description = "订单ID")
    private Long orderId;

    /** 订单号快照 */
            @Excel(name = "订单号快照")
    @Schema(description = "订单号快照")
    private String orderNo;

    /** 财务流水ID */
            @Excel(name = "财务流水ID")
    @Schema(description = "财务流水ID")
    private Long financeFlowId;

    /** 业务类型，1订单收入 2退款扣减 3人工调整 */
            @Excel(name = "业务类型，1订单收入 2退款扣减 3人工调整")
    @Schema(description = "业务类型，1订单收入 2退款扣减 3人工调整")
    private String businessType;

    /** 本行结算金额 */
            @Excel(name = "本行结算金额")
    @Schema(description = "本行结算金额")
    private BigDecimal settlementAmount;

    /** 来源交易时间 */
            @JsonFormat(pattern = "yyyy-MM-dd")
            @Excel(name = "来源交易时间", width = 30, dateFormat = "yyyy-MM-dd")
    @Schema(description = "来源交易时间")
    private Date sourceTime;

        }
