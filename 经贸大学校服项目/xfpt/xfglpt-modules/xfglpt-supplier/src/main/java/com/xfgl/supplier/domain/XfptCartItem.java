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
 * 购物车明细对象 xfpt_cart_item
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
@TableName("xfpt_cart_item")
@Schema(description = "购物车明细")
public class XfptCartItem extends BaseEntity
        {
private static final long serialVersionUID = 1L;

    /** 主键 */
        @TableId(type= IdType.AUTO)
    @Schema(description = "主键")
    private Long id;

    /** 家长用户ID */
            @Excel(name = "家长用户ID")
    @Schema(description = "家长用户ID")
    private Long userId;

    /** 学生ID */
            @Excel(name = "学生ID")
    @Schema(description = "学生ID")
    private Long studentId;

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

    /** 当前单价 */
            @Excel(name = "当前单价")
    @Schema(description = "当前单价")
    private BigDecimal unitPrice;

    /** 购买数量 */
            @Excel(name = "购买数量")
    @Schema(description = "购买数量")
    private String buyNum;

    /** 选中状态（0未选中 1选中） */
            @Excel(name = "选中状态", readConverterExp = "0=未选中,1=选中")
    @Schema(description = "选中状态（0未选中 1选中）")
    private String checkedFlag;

        }
