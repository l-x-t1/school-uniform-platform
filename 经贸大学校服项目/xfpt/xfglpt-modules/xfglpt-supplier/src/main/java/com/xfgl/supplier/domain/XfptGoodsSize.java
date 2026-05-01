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
 * 商品尺码对象 xfpt_goods_size
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
@TableName("xfpt_goods_size")
@Schema(description = "商品尺码")
public class XfptGoodsSize extends BaseEntity
        {
private static final long serialVersionUID = 1L;

    /** 主键 */
        @TableId(type= IdType.AUTO)
    @Schema(description = "主键")
    private Long id;

    /** 商品ID */
            @Excel(name = "商品ID")
    @Schema(description = "商品ID")
    private Long goodsId;

    /** 尺码编码 */
            @Excel(name = "尺码编码")
    @Schema(description = "尺码编码")
    private String sizeCode;

    /** 尺码名称 */
            @Excel(name = "尺码名称")
    @Schema(description = "尺码名称")
    private String sizeName;

    /** 性别类型（0通用 1男 2女） */
            @Excel(name = "性别类型", readConverterExp = "0=通用,1=男,2=女")
    @Schema(description = "性别类型（0通用 1男 2女）")
    private String genderType;

    /** 量体规则JSON文本 */
            @Excel(name = "量体规则JSON文本")
    @Schema(description = "量体规则JSON文本")
    private String measureJson;

    /** 零售价 */
            @Excel(name = "零售价")
    @Schema(description = "零售价")
    private BigDecimal retailPrice;

    /** 统购价 */
            @Excel(name = "统购价")
    @Schema(description = "统购价")
    private BigDecimal groupPrice;

    /** 库存 */
            @Excel(name = "库存")
    @Schema(description = "库存")
    private String stockNum;

    /** 状态（0停用 1启用） */
            @Excel(name = "状态", readConverterExp = "0=停用,1=启用")
    @Schema(description = "状态（0停用 1启用）")
    private String status;

        }
