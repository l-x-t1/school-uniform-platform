package com.xfgl.school.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xfgl.common.core.annotation.Excel;
import com.xfgl.common.core.web.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 发货单对象 xfpt_delivery_order
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("xfpt_delivery_order")
@Schema(description = "发货单")
public class XfptDeliveryOrder extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键")
    private Long id;

    @Excel(name = "发货单号")
    @Schema(description = "发货单号")
    private String deliveryNo;

    @Excel(name = "统购公告ID")
    @Schema(description = "统购公告ID")
    private Long noticeId;

    @Excel(name = "订单ID")
    @Schema(description = "订单ID")
    private Long orderId;

    @Excel(name = "订单号快照")
    @Schema(description = "订单号快照")
    private String orderNo;

    @Excel(name = "生产单ID")
    @Schema(description = "生产单ID")
    private Long productionId;

    @Excel(name = "学校ID")
    @Schema(description = "学校ID")
    private Long schoolId;

    @Excel(name = "供应商ID")
    @Schema(description = "供应商ID")
    private Long supplierId;

    @Excel(name = "收货人姓名快照")
    @Schema(description = "收货人姓名快照")
    private String receiverName;

    @Excel(name = "收货人电话快照")
    @Schema(description = "收货人电话快照")
    private String receiverPhone;

    @Excel(name = "收货地址快照")
    @Schema(description = "收货地址快照")
    private String receiverAddress;

    @Excel(name = "物流公司")
    @Schema(description = "物流公司")
    private String logisticsCompany;

    @Excel(name = "物流单号")
    @Schema(description = "物流单号")
    private String logisticsNo;

    @Excel(name = "发货状态")
    @Schema(description = "发货状态，0待发货，1已发货，2已签收")
    private String deliveryStatus;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "发货时间", width = 30, dateFormat = "yyyy-MM-dd")
    @Schema(description = "发货时间")
    private Date deliveryTime;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "签收时间", width = 30, dateFormat = "yyyy-MM-dd")
    @Schema(description = "签收时间")
    private Date signTime;

    @Excel(name = "运费")
    @Schema(description = "运费")
    private BigDecimal carriageFee;

    @Excel(name = "当前归属部门/学校")
    @Schema(description = "当前归属部门/学校")
    private Long deptId;

    @Schema(description = "删除标志")
    private String delFlag;

    @TableField(exist = false)
    @Schema(description = "发货单明细")
    private List<XfptDeliveryOrderItem> xfptDeliveryOrderItemList;
}
