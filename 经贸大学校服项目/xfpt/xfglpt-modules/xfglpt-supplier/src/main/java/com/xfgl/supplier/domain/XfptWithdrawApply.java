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
 * 提现申请对象 xfpt_withdraw_apply
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
@TableName("xfpt_withdraw_apply")
@Schema(description = "提现申请")
public class XfptWithdrawApply extends BaseEntity
        {
private static final long serialVersionUID = 1L;

    /** 主键 */
        @TableId(type= IdType.AUTO)
    @Schema(description = "主键")
    private Long id;

    /** 提现单号 */
            @Excel(name = "提现单号")
    @Schema(description = "提现单号")
    private String withdrawNo;

    /** 供应商ID */
            @Excel(name = "供应商ID")
    @Schema(description = "供应商ID")
    private Long supplierId;

    /** 申请提现金额 */
            @Excel(name = "申请提现金额")
    @Schema(description = "申请提现金额")
    private BigDecimal applyAmount;

    /** 银行账户信息JSON文本 */
            @Excel(name = "银行账户信息JSON文本")
    @Schema(description = "银行账户信息JSON文本")
    private String bankInfoJson;

    /** 状态（0待审核 1已通过 2已驳回 3已打款） */
            @Excel(name = "状态", readConverterExp = "0=待审核,1=已通过,2=已驳回,3=已打款")
    @Schema(description = "状态（0待审核 1已通过 2已驳回 3已打款）")
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

    /** 打款时间 */
            @JsonFormat(pattern = "yyyy-MM-dd")
            @Excel(name = "打款时间", width = 30, dateFormat = "yyyy-MM-dd")
    @Schema(description = "打款时间")
    private Date payTime;

    /** 审核意见 */
            @Excel(name = "审核意见")
    @Schema(description = "审核意见")
    private String auditRemark;

    /** 财务流水ID */
            @Excel(name = "财务流水ID")
    @Schema(description = "财务流水ID")
    private Long financeFlowId;

        }
