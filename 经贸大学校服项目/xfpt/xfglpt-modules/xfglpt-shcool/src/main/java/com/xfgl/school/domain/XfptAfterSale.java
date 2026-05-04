package com.xfgl.school.domain;
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
 * 售后主对象 xfpt_after_sale
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
@TableName("xfpt_after_sale")
@Schema(description = "售后主")
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

    /** 订单主表ID */
            @Excel(name = "订单主表ID")
    @Schema(description = "订单主表ID")
    private Long orderId;

    /** 订单明细ID */
            @Excel(name = "订单明细ID")
    @Schema(description = "订单明细ID")
    private Long orderItemId;

    /** 订单编号快照 */
            @Excel(name = "订单编号快照")
    @Schema(description = "订单编号快照")
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

    /** 发起用户ID */
            @Excel(name = "发起用户ID")
    @Schema(description = "发起用户ID")
    private Long userId;

    /** 发起用户名快照 */
            @Excel(name = "发起用户名快照")
    @Schema(description = "发起用户名快照")
    private String userName;

    /** 学生姓名快照 */
            @Excel(name = "学生姓名快照")
    @Schema(description = "学生姓名快照")
    private String studentName;

    /** 申请类型，1仅退款 2退货退款 3换货 */
            @Excel(name = "申请类型，1仅退款 2退货退款 3换货")
    @Schema(description = "申请类型，1仅退款 2退货退款 3换货")
    private String applyType;

    /** 换货目标尺码ID */
            @Excel(name = "换货目标尺码ID")
    @Schema(description = "换货目标尺码ID")
    private Long targetSizeId;

    /** 换货目标尺码名称 */
            @Excel(name = "换货目标尺码名称")
    @Schema(description = "换货目标尺码名称")
    private String targetSizeName;

    /** 申请原因 */
            @Excel(name = "申请原因")
    @Schema(description = "申请原因")
    private String reason;

    /** 图片凭证JSON文本 */
    @Schema(description = "图片凭证JSON文本")
    private String imagesJson;

    /** 申请数量 */
            @Excel(name = "申请数量")
    @Schema(description = "申请数量")
    private Integer applyNum;

    /** 申请金额 */
            @Excel(name = "申请金额")
    @Schema(description = "申请金额")
    private BigDecimal applyAmount;

    /** 实际退款金额 */
            @Excel(name = "实际退款金额")
    @Schema(description = "实际退款金额")
    private BigDecimal actualRefundAmount;

    /** 退款状态，0未退款 1退款中 2退款成功 3退款失败 */
            @Excel(name = "退款状态，0未退款 1退款中 2退款成功 3退款失败")
    @Schema(description = "退款状态，0未退款 1退款中 2退款成功 3退款失败")
    private String refundStatus;

    /** 退款完成时间 */
            @JsonFormat(pattern = "yyyy-MM-dd")
            @Excel(name = "退款完成时间", width = 30, dateFormat = "yyyy-MM-dd")
    @Schema(description = "退款完成时间")
    private Date refundTime;

    /** 售后状态，0待审核 1已通过 2已驳回 3处理中 4已完成 5已关闭 */
            @Excel(name = "售后状态，0待审核 1已通过 2已驳回 3处理中 4已完成 5已关闭")
    @Schema(description = "售后状态，0待审核 1已通过 2已驳回 3处理中 4已完成 5已关闭")
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

        }
