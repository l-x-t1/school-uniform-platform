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
 * 统购公告商品明细对象 xfpt_group_notice_item
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
@TableName("xfpt_group_notice_item")
@Schema(description = "统购公告商品明细")
public class XfptGroupNoticeItem extends BaseEntity
        {
private static final long serialVersionUID = 1L;

    /** 主键 */
        @TableId(type= IdType.AUTO)
    @Schema(description = "主键")
    private Long id;

    /** 公告ID */
            @Excel(name = "公告ID")
    @Schema(description = "公告ID")
    private Long noticeId;

    /** 商品ID */
            @Excel(name = "商品ID")
    @Schema(description = "商品ID")
    private Long goodsId;

    /** 商品名称快照 */
            @Excel(name = "商品名称快照")
    @Schema(description = "商品名称快照")
    private String goodsName;

    /** 尺码规则JSON文本 */
            @Excel(name = "尺码规则JSON文本")
    @Schema(description = "尺码规则JSON文本")
    private String sizeRuleJson;

    /** 单位 */
            @Excel(name = "单位")
    @Schema(description = "单位")
    private String unitName;

    /** 采购数量 */
            @Excel(name = "采购数量")
    @Schema(description = "采购数量")
    private String buyNum;

    /** 适用年级ID */
            @Excel(name = "适用年级ID")
    @Schema(description = "适用年级ID")
    private String gradeIds;

    /** 适用年级名称 */
            @Excel(name = "适用年级名称")
    @Schema(description = "适用年级名称")
    private String gradeNames;

    /** 排序值 */
            @Excel(name = "排序值")
    @Schema(description = "排序值")
    private String sortNum;

    /** 部门ID */
            @Excel(name = "部门ID")
    @Schema(description = "部门ID")
    private Long deptId;

    /** 祖级列表 */
            @Excel(name = "祖级列表")
    @Schema(description = "祖级列表")
    private String ancestors;

        }
