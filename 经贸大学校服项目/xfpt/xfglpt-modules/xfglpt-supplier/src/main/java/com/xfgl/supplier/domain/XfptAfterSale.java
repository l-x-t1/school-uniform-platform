package com.xfgl.supplier.domain;

import java.math.BigDecimal;
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
 * 售后单对象 xfpt_after_sale
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
@TableName("xfpt_after_sale")
@Schema(description = "售后单")
public class XfptAfterSale extends BaseEntity
        {
private static final long serialVersionUID = 1L;

    /** 主键 */
        @TableId(type= IdType.AUTO)
    @Schema(description = "主键")
    private Long id;

    /** 售后单号 */
            @Excel(name = "售后单号")
    @Schema(description = "售后单号")
    private String afterSaleNo;

    /** 订单ID */
            @Excel(name = "订单ID")
    @Schema(description = "订单ID")
    private Long orderId;

    /** 订单明细ID */
            @Excel(name = "订单明细ID")
    @Schema(description = "订单明细ID")
    private Long orderItemId;

    /** 订单号快照 */
            @Excel(name = "订单号快照")
    @Schema(description = "订单号快照")
    private String orderNo;

    /** 订单类型，1统购 2零售 */
            @Excel(name = "订单类型，1统购 2零售")
    @Schema(description = "订单类型，1统购 2零售")
    private String orderType;

    /** 学校ID */
            @Excel(name = "学校ID")
    @Schema(description = "学校ID")
    private Long schoolId;

    /** 供应商ID */
            @Excel(name = "供应商ID")
    @Schema(description = "供应商ID")
    private Long supplierId;

    /** 申请人用户ID */
            @Excel(name = "申请人用户ID")
    @Schema(description = "申请人用户ID")
    private Long userId;

    /** 申请人姓名 */
            @Excel(name = "申请人姓名")
    @Schema(description = "申请人姓名")
    private String userName;

    /** 学生姓名快照 */
            @Excel(name = "学生姓名快照")
    @Schema(description = "学生姓名快照")
    private String studentName;

    /** 申请类型，1退货 2换货 3退款 4尺码调换 */
            @Excel(name = "申请类型，1退货 2换货 3退款 4尺码调换")
    @Schema(description = "申请类型，1退货 2换货 3退款 4尺码调换")
    private String applyType;

    /** 目标尺码ID */
            @Excel(name = "目标尺码ID")
    @Schema(description = "目标尺码ID")
    private Long targetSizeId;

    /** 目标尺码名称快照 */
            @Excel(name = "目标尺码名称快照")
    @Schema(description = "目标尺码名称快照")
    private String targetSizeName;

    /** 申请原因 */
            @Excel(name = "申请原因")
    @Schema(description = "申请原因")
    private String reason;

    /** 凭证图片JSON文本 */
            @Excel(name = "凭证图片JSON文本")
    @Schema(description = "凭证图片JSON文本")
    private String imagesJson;

    /** 申请数量 */
            @Excel(name = "申请数量")
    @Schema(description = "申请数量")
    private Integer applyNum;

    /** 申请金额 */
            @Excel(name = "申请金额")
    @Schema(description = "申请金额")
    private BigDecimal applyAmount;

    /** 状态，0待审核 1通过 2驳回 3处理中 4已完成 */
            @Excel(name = "状态，0待审核 1通过 2驳回 3处理中 4已完成")
    @Schema(description = "状态，0待审核 1通过 2驳回 3处理中 4已完成")
    private String status;

    /** 审核人 */
            @Excel(name = "审核人")
    @Schema(description = "审核人")
    private String auditBy;

    /** 审核时间 */
            @JsonFormat(pattern = "yyyy-MM-dd")
            @Excel(name = "审核时间", width = 30, dateFormat = "yyyy-MM-dd")
    @Schema(description = "审核时间")
    private Date auditTime;

    /** 处理结果 */
            @Excel(name = "处理结果")
    @Schema(description = "处理结果")
    private String handleResult;

    /** 处理备注 */
            @Excel(name = "处理备注")
    @Schema(description = "处理备注")
    private String handleRemark;

    /** 完成时间 */
            @JsonFormat(pattern = "yyyy-MM-dd")
            @Excel(name = "完成时间", width = 30, dateFormat = "yyyy-MM-dd")
    @Schema(description = "完成时间")
    private Date finishTime;

    /** 当前归属部门/学校 */
            @Excel(name = "当前归属部门/学校")
    @Schema(description = "当前归属部门/学校")
    private Long deptId;

    /** 组织祖级链路 */
            @Excel(name = "组织祖级链路")
    @Schema(description = "组织祖级链路")
    private String ancestors;

        }
