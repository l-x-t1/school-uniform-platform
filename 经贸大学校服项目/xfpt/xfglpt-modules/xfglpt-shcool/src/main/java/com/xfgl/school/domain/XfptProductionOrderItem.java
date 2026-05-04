package com.xfgl.school.domain;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import com.xfgl.common.core.annotation.Excel;
import com.xfgl.common.core.web.domain.BaseEntity;

/**
 * 生产单明细对象 xfpt_production_order_item
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
@TableName("xfpt_production_order_item")
@Schema(description = "生产单明细")
public class XfptProductionOrderItem extends BaseEntity
        {
private static final long serialVersionUID = 1L;

    /** 主键 */
        @TableId(type= IdType.AUTO)
    @Schema(description = "主键")
    private Long id;

    /** 生产单主表ID */
            @Excel(name = "生产单主表ID")
    @Schema(description = "生产单主表ID")
    private Long productionId;

    /** 订单明细ID */
            @Excel(name = "订单明细ID")
    @Schema(description = "订单明细ID")
    private Long orderItemId;

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

    /** 应生产数量 */
            @Excel(name = "应生产数量")
    @Schema(description = "应生产数量")
    private Integer buyNum;

    /** 已完成数量 */
            @Excel(name = "已完成数量")
    @Schema(description = "已完成数量")
    private Integer finishNum;

    /** 状态，0待生产 1生产中 2生产完成 */
            @Excel(name = "状态，0待生产 1生产中 2生产完成")
    @Schema(description = "状态，0待生产 1生产中 2生产完成")
    private String status;

}
