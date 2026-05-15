package com.xfgl.school.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xfgl.common.core.annotation.Excel;
import com.xfgl.common.core.web.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 发货单明细对象 xfpt_delivery_order_item
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("xfpt_delivery_order_item")
@Schema(description = "发货单明细")
public class XfptDeliveryOrderItem extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键")
    private Long id;

    @Excel(name = "发货单主表ID")
    @Schema(description = "发货单主表ID")
    private Long deliveryId;

    @Excel(name = "订单明细ID")
    @Schema(description = "订单明细ID")
    private Long orderItemId;

    @Excel(name = "商品ID")
    @Schema(description = "商品ID")
    private Long goodsId;

    @Excel(name = "尺码ID")
    @Schema(description = "尺码ID")
    private Long sizeId;

    @Excel(name = "商品名称快照")
    @Schema(description = "商品名称快照")
    private String goodsName;

    @Excel(name = "尺码名称快照")
    @Schema(description = "尺码名称快照")
    private String sizeName;

    @Excel(name = "发货数量")
    @Schema(description = "发货数量")
    private Integer deliveryNum;

    @Excel(name = "签收数量")
    @Schema(description = "签收数量")
    private Integer signNum;

    @Excel(name = "状态")
    @Schema(description = "状态，0待发货，1已发货，2已签收")
    private String status;

}
