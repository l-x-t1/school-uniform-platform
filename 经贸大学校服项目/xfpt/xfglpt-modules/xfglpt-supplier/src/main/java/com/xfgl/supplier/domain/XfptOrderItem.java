package com.xfgl.supplier.domain;

import java.math.BigDecimal;
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
 * 订单明细对象 xfpt_order_item
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
@TableName("xfpt_order_item")
@Schema(description = "订单明细")
public class XfptOrderItem extends BaseEntity
        {
private static final long serialVersionUID = 1L;

    /** 主键 */
        @TableId(type= IdType.AUTO)
    @Schema(description = "主键")
    private Long id;

    /** 订单主表ID */
            @Excel(name = "订单主表ID")
    @Schema(description = "订单主表ID")
    private Long orderId;

    /** 商品ID */
            @Excel(name = "商品ID")
    @Schema(description = "商品ID")
    private Long goodsId;

    /** 尺码ID */
            @Excel(name = "尺码ID")
    @Schema(description = "尺码ID")
    private Long sizeId;

    /** 商品名称快照 */
            @Excel(name = "商品名称快照")
    @Schema(description = "商品名称快照")
    private String goodsName;

    /** 尺码名称快照 */
            @Excel(name = "尺码名称快照")
    @Schema(description = "尺码名称快照")
    private String sizeName;

    /** 商品主图快照 */
            @Excel(name = "商品主图快照")
    @Schema(description = "商品主图快照")
    private String mainImage;

    /** 销售类型，1统购 2零售 */
            @Excel(name = "销售类型，1统购 2零售")
    @Schema(description = "销售类型，1统购 2零售")
    private String saleType;

    /** 单价 */
            @Excel(name = "单价")
    @Schema(description = "单价")
    private BigDecimal unitPrice;

    /** 购买数量 */
            @Excel(name = "购买数量")
    @Schema(description = "购买数量")
    private Integer buyNum;

    /** 行金额 */
            @Excel(name = "行金额")
    @Schema(description = "行金额")
    private BigDecimal itemAmount;

    /** 当前归属部门/学校 */
            @Excel(name = "当前归属部门/学校")
    @Schema(description = "当前归属部门/学校")
    private Long deptId;

    /** 组织祖级链路 */
            @Excel(name = "组织祖级链路")
    @Schema(description = "组织祖级链路")
    private String ancestors;

        }
