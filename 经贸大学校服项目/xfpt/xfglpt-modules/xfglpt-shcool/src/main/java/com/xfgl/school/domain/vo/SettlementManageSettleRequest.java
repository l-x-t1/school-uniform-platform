package com.xfgl.school.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 结算管理结算请求
 */
@Data
@Schema(description = "结算管理结算请求")
public class SettlementManageSettleRequest {
    /** 申请结算记录ID */
    @Schema(description = "申请结算记录ID")
    private Long id;

    /** 支付渠道 */
    @Schema(description = "支付渠道")
    private String payChannel;

    /** 外部交易单号 */
    @Schema(description = "外部交易单号")
    private String tradeNo;

    /** 结算备注 */
    @Schema(description = "结算备注")
    private String remark;
}
