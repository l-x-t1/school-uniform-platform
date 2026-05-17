package com.xfgl.school.domain.vo;

import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 教育局结算统计
 *
 * @author codex
 * @date 2026-05-17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "教育局结算统计")
public class EduSettlementStatisticsVO {
    @Schema(description = "结算金额")
    private BigDecimal settlementAmount;

    @Schema(description = "税额")
    private BigDecimal taxAmount;

    @Schema(description = "已付金额")
    private BigDecimal paidAmount;

    @Schema(description = "未付金额")
    private BigDecimal unpaidAmount;

    @Schema(description = "总记录数")
    private Long totalCount;

    @Schema(description = "待结算记录数")
    private Long pendingCount;

    @Schema(description = "部分结算记录数")
    private Long partialCount;

    @Schema(description = "已结算记录数")
    private Long settledCount;
}
