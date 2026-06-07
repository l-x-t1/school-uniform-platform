package com.xfgl.school.domain;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
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
 * 供应商提现申请对象 xfpt_withdraw_apply
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
@TableName("xfpt_withdraw_apply")
@Schema(description = "供应商提现申请")
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

    /** 申请提现供应商ID */
            @Excel(name = "申请提现供应商ID")
    @Schema(description = "申请提现供应商ID")
    private Long supplierId;

    /** 供应商名称快照 */
            @Excel(name = "供应商名称快照")
    @Schema(description = "供应商名称快照")
    private String supplierName;

    /** 申请提现金额 */
            @Excel(name = "申请提现金额")
    @Schema(description = "申请提现金额")
    private BigDecimal applyAmount;

    /** 银行卡或收款账户信息JSON快照 */
    @Schema(description = "银行卡或收款账户信息JSON快照")
    private String bankInfoJson;

    /** 提现状态，0待审核 1审核通过 2审核拒绝 3已打款 4已取消 */
            @Excel(name = "提现状态，0待审核 1审核通过 2审核拒绝 3已打款 4已取消")
    @Schema(description = "提现状态，0待审核 1审核通过 2审核拒绝 3已打款 4已取消")
    private String status;

    /** 申请时间 */
            @JsonFormat(pattern = "yyyy-MM-dd")
            @Excel(name = "申请时间", width = 30, dateFormat = "yyyy-MM-dd")
    @Schema(description = "申请时间")
    private Date applyTime;

    /** 审核人ID */
            @Excel(name = "审核人ID")
    @Schema(description = "审核人ID")
    private Long auditUserId;

    /** 审核人姓名快照 */
            @Excel(name = "审核人姓名快照")
    @Schema(description = "审核人姓名快照")
    private String auditUserName;

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

    /** 审核备注 */
            @Excel(name = "审核备注")
    @Schema(description = "审核备注")
    private String auditRemark;

    /** 关联资金流水ID */
            @Excel(name = "关联资金流水ID")
    @Schema(description = "关联资金流水ID")
    private Long financeFlowId;

    @TableField(exist = false)
    @Schema(description = "瑙嗗浘妯″紡")
    private String viewMode;

    @TableField(exist = false)
    @Schema(description = "绠＄悊鍛樻ā鎷熷鏍℃椂鐨勫鏍D")
    private Long scopeSchoolId;

    @TableField(exist = false)
    @Schema(description = "鏁欒偛灞€鍙瀛︽牎ID闆嗗悎")
    private List<Long> scopeSchoolIds;

    @TableField(exist = false)
    @Schema(description = "褰掑睘瀛︽牎ID")
    private Long schoolId;

    @TableField(exist = false)
    @Schema(description = "褰掑睘瀛︽牎鍚嶇О")
    private String schoolName;

    @TableField(exist = false)
    @Schema(description = "鍏宠仈娴佹按鍗曞彿")
    private String flowNo;

    @TableField(exist = false)
    @Schema(description = "鍏宠仈璁㈠崟鍙?")
    private String orderNo;

        }
