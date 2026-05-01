package com.xfgl.supplier.domain;

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
 * 学校供应商合作商品对象 xfpt_school_supplier_goods_rel
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
@TableName("xfpt_school_supplier_goods_rel")
@Schema(description = "学校供应商合作商品")
public class XfptSchoolSupplierGoodsRel extends BaseEntity
        {
private static final long serialVersionUID = 1L;

    /** 主键 */
        @TableId(type= IdType.AUTO)
    @Schema(description = "主键")
    private Long id;

    /** 合作关系主表ID */
            @Excel(name = "合作关系主表ID")
    @Schema(description = "合作关系主表ID")
    private Long relId;

    /** 学校ID */
            @Excel(name = "学校ID")
    @Schema(description = "学校ID")
    private Long schoolId;

    /** 供应商ID */
            @Excel(name = "供应商ID")
    @Schema(description = "供应商ID")
    private Long supplierId;

    /** 商品ID */
            @Excel(name = "商品ID")
    @Schema(description = "商品ID")
    private Long goodsId;

    /** 状态，0停用 1启用 */
            @Excel(name = "状态，0停用 1启用")
    @Schema(description = "状态，0停用 1启用")
    private String status;

    /** 当前归属部门/学校 */
            @Excel(name = "当前归属部门/学校")
    @Schema(description = "当前归属部门/学校")
    private Long deptId;

    /** 组织祖级链路 */
            @Excel(name = "组织祖级链路")
    @Schema(description = "组织祖级链路")
    private String ancestors;

        }
