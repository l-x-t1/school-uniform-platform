package com.xfgl.school.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xfgl.common.core.web.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 发货单统计展示对象
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@Schema(description = "发货单统计")
public class XfptDeliveryStatisticsVo extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "发货单号")
    private String deliveryNo;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "学校ID")
    private Long schoolId;

    @Schema(description = "学校名称")
    private String schoolName;

    @Schema(description = "供应商名称")
    private String supplierName;

    @Schema(description = "发货状态")
    private String deliveryStatus;

    @Schema(description = "发货总数")
    private Long deliveryTotalNum;

    @Schema(description = "签收总数")
    private Long signTotalNum;

    @Schema(description = "运费")
    private BigDecimal carriageFee;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "发货时间")
    private Date deliveryTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "签收时间")
    private Date signTime;
}
