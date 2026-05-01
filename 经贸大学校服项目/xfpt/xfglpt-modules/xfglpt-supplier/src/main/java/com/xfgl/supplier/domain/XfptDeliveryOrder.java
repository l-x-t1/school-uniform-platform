package com.xfgl.supplier.domain;

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
 * 发货单对象 xfpt_delivery_order
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
@TableName("xfpt_delivery_order")
@Schema(description = "发货单")
public class XfptDeliveryOrder extends BaseEntity
        {
private static final long serialVersionUID = 1L;

    /** 主键 */
        @TableId(type= IdType.AUTO)
    @Schema(description = "主键")
    private Long id;

    /** 发货单号 */
            @Excel(name = "发货单号")
    @Schema(description = "发货单号")
    private String deliveryNo;

    /** 统购公告ID */
            @Excel(name = "统购公告ID")
    @Schema(description = "统购公告ID")
    private Long noticeId;

    /** 订单ID */
            @Excel(name = "订单ID")
    @Schema(description = "订单ID")
    private Long orderId;

    /** 订单号快照 */
            @Excel(name = "订单号快照")
    @Schema(description = "订单号快照")
    private String orderNo;

    /** 生产单ID */
            @Excel(name = "生产单ID")
    @Schema(description = "生产单ID")
    private Long productionId;

    /** 学校ID */
            @Excel(name = "学校ID")
    @Schema(description = "学校ID")
    private Long schoolId;

    /** 供应商ID */
            @Excel(name = "供应商ID")
    @Schema(description = "供应商ID")
    private Long supplierId;

    /** 收货人姓名快照 */
            @Excel(name = "收货人姓名快照")
    @Schema(description = "收货人姓名快照")
    private String receiverName;

    /** 收货人电话快照 */
            @Excel(name = "收货人电话快照")
    @Schema(description = "收货人电话快照")
    private String receiverPhone;

    /** 收货地址快照 */
            @Excel(name = "收货地址快照")
    @Schema(description = "收货地址快照")
    private String receiverAddress;

    /** 物流公司 */
            @Excel(name = "物流公司")
    @Schema(description = "物流公司")
    private String logisticsCompany;

    /** 物流单号 */
            @Excel(name = "物流单号")
    @Schema(description = "物流单号")
    private String logisticsNo;

    /** 发货状态，0待发货 1已发货 2已签收 */
            @Excel(name = "发货状态，0待发货 1已发货 2已签收")
    @Schema(description = "发货状态，0待发货 1已发货 2已签收")
    private String deliveryStatus;

    /** 发货时间 */
            @JsonFormat(pattern = "yyyy-MM-dd")
            @Excel(name = "发货时间", width = 30, dateFormat = "yyyy-MM-dd")
    @Schema(description = "发货时间")
    private Date deliveryTime;

    /** 签收时间 */
            @JsonFormat(pattern = "yyyy-MM-dd")
            @Excel(name = "签收时间", width = 30, dateFormat = "yyyy-MM-dd")
    @Schema(description = "签收时间")
    private Date signTime;

    /** 当前归属部门/学校 */
            @Excel(name = "当前归属部门/学校")
    @Schema(description = "当前归属部门/学校")
    private Long deptId;

    /** 组织祖级链路 */
            @Excel(name = "组织祖级链路")
    @Schema(description = "组织祖级链路")
    private String ancestors;

        }
