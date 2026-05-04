package com.xfgl.school.domain;
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
 * 资金流水对象 xfpt_finance_flow
 *
 * @author yinet
 * @date 2026-05-04
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("xfpt_finance_flow")
@Schema(description = "资金流水")
public class XfptFinanceFlow extends BaseEntity
        {
private static final long serialVersionUID = 1L;

    /** 主键 */
        @TableId(type= IdType.AUTO)
    @Schema(description = "主键")
    private Long id;

    /** 流水单号 */
            @Excel(name = "流水单号")
    @Schema(description = "流水单号")
    private String flowNo;

    /** 业务类型，1支付 2退款 3提现 4结算 */
            @Excel(name = "业务类型，1支付 2退款 3提现 4结算")
    @Schema(description = "业务类型，1支付 2退款 3提现 4结算")
    private String businessType;

    /** 收支方向，1收入 2支出 */
            @Excel(name = "收支方向，1收入 2支出")
    @Schema(description = "收支方向，1收入 2支出")
    private String flowDirection;

    /** 关联订单ID */
            @Excel(name = "关联订单ID")
    @Schema(description = "关联订单ID")
    private Long orderId;

    /** 订单编号快照 */
            @Excel(name = "订单编号快照")
    @Schema(description = "订单编号快照")
    private String orderNo;

    /** 订单类型，1统购 2零售 */
            @Excel(name = "订单类型，1统购 2零售")
    @Schema(description = "订单类型，1统购 2零售")
    private String orderType;

    /** 关联售后单ID */
            @Excel(name = "关联售后单ID")
    @Schema(description = "关联售后单ID")
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

    /** 流水金额 */
            @Excel(name = "流水金额")
    @Schema(description = "流水金额")
    private BigDecimal amount;

    /** 支付渠道，如微信、支付宝、线下转账 */
            @Excel(name = "支付渠道，如微信、支付宝、线下转账")
    @Schema(description = "支付渠道，如微信、支付宝、线下转账")
    private String payChannel;

    /** 外部交易单号或平台交易号 */
            @Excel(name = "外部交易单号或平台交易号")
    @Schema(description = "外部交易单号或平台交易号")
    private String tradeNo;

    /** 收款方类型，1学校 2供应商；统购收学校，零售收供应商 */
            @Excel(name = "收款方类型，1学校 2供应商；统购收学校，零售收供应商")
    @Schema(description = "收款方类型，1学校 2供应商；统购收学校，零售收供应商")
    private String payeeType;

    /** 收款方ID，学校ID或供应商ID */
            @Excel(name = "收款方ID，学校ID或供应商ID")
    @Schema(description = "收款方ID，学校ID或供应商ID")
    private Long payeeId;

    /** 收款方名称快照 */
            @Excel(name = "收款方名称快照")
    @Schema(description = "收款方名称快照")
    private String payeeName;

    /** 收款账户户名快照 */
            @Excel(name = "收款账户户名快照")
    @Schema(description = "收款账户户名快照")
    private String payeeAccountName;

    /** 收款银行快照 */
            @Excel(name = "收款银行快照")
    @Schema(description = "收款银行快照")
    private String payeeBankName;

    /** 收款银行卡号快照 */
    @Schema(description = "收款银行卡号快照")
    private String payeeBankAccountNo;

    /** 收款码快照；统购展示学校收款码，零售展示供应商收款码 */
    @Schema(description = "收款码快照；统购展示学校收款码，零售展示供应商收款码")
    private String receiptQrCode;

    /** 流水状态，0未支付 1已到达学校账号 2已到达供应商账号 */
            @Excel(name = "流水状态，0未支付 1已到达学校账号 2已到达供应商账号")
    @Schema(description = "流水状态，0未支付 1已到达学校账号 2已到达供应商账号")
    private String status;

    /** 交易发生时间 */
            @JsonFormat(pattern = "yyyy-MM-dd")
            @Excel(name = "交易发生时间", width = 30, dateFormat = "yyyy-MM-dd")
    @Schema(description = "交易发生时间")
    private Date tradeTime;

    /** 当前归属部门/学校 */
            @Excel(name = "当前归属部门/学校")
    @Schema(description = "当前归属部门/学校")
    private Long deptId;

        }
