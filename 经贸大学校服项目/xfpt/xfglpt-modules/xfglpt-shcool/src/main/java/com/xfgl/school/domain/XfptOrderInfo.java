package com.xfgl.school.domain;
import java.math.BigDecimal;
import java.util.List;
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
 * 订单信息对象 xfpt_order_info
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
@TableName("xfpt_order_info")
@Schema(description = "订单信息")
public class XfptOrderInfo extends BaseEntity
        {
private static final long serialVersionUID = 1L;

    /** 主键 */
        @TableId(type= IdType.AUTO)
    @Schema(description = "主键")
    private Long id;

    /** 订单号 */
            @Excel(name = "订单号")
    @Schema(description = "订单号")
    private String orderNo;

    /** 订单类型，1统购 2零售 */
            @Excel(name = "订单类型，1统购 2零售")
    @Schema(description = "订单类型，1统购 2零售")
    private String orderType;

    /** 统购公告ID */
            @Excel(name = "统购公告ID")
    @Schema(description = "统购公告ID")
    private Long noticeId;

    /** 学校ID */
            @Excel(name = "学校ID")
    @Schema(description = "学校ID")
    private Long schoolId;

    /** 学校名称快照 */
            @Excel(name = "学校名称快照")
    @Schema(description = "学校名称快照")
    private String schoolName;

    /** 供应商ID */
            @Excel(name = "供应商ID")
    @Schema(description = "供应商ID")
    private Long supplierId;

    /** 供应商名称快照 */
            @Excel(name = "供应商名称快照")
    @Schema(description = "供应商名称快照")
    private String supplierName;

    /** 下单用户ID */
            @Excel(name = "下单用户ID")
    @Schema(description = "下单用户ID")
    private Long userId;

    /** 下单用户名称 */
            @Excel(name = "下单用户名称")
    @Schema(description = "下单用户名称")
    private String userName;

    /** 学生ID */
            @Excel(name = "学生ID")
    @Schema(description = "学生ID")
    private Long studentId;

    /** 学生姓名 */
            @Excel(name = "学生姓名")
    @Schema(description = "学生姓名")
    private String studentName;

    /** 年级名称 */
            @Excel(name = "年级名称")
    @Schema(description = "年级名称")
    private String gradeName;

    /** 班级名称 */
            @Excel(name = "班级名称")
    @Schema(description = "班级名称")
    private String className;

    /** 收货地址ID */
            @Excel(name = "收货地址ID")
    @Schema(description = "收货地址ID")
    private Long addressId;

    /** 收货类型，1学校 2家庭 */
            @Excel(name = "收货类型，1学校 2家庭")
    @Schema(description = "收货类型，1学校 2家庭")
    private String receiveType;

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

    /** 订单总金额（衣服价格加上运费） */
            @Excel(name = "订单总金额", readConverterExp = "衣=服价格加上运费")
    @Schema(description = "订单总金额（衣服价格加上运费）")
    private BigDecimal totalAmount;

    /** 实付金额 */
            @Excel(name = "实付金额")
    @Schema(description = "实付金额")
    private BigDecimal payAmount;

    /** 已退款金额 */
            @Excel(name = "已退款金额")
    @Schema(description = "已退款金额")
    private BigDecimal refundAmount;

    /** 支付状态，0待支付 1已支付 2部分退款 3已退款 */
            @Excel(name = "支付状态，0待支付 1已支付 2部分退款 3已退款")
    @Schema(description = "支付状态，0待支付 1已支付 2部分退款 3已退款")
    private String payStatus;

    /** 订单状态，0待支付 1待生产 2待发货 3待收货 4已完成 5已取消 6售后中 */
            @Excel(name = "订单状态，0待支付 1待生产 2待发货 3待收货 4已完成 5已取消 6售后中")
    @Schema(description = "订单状态，0待支付 1待生产 2待发货 3待收货 4已完成 5已取消 6售后中")
    private String orderStatus;

    /** 支付时间 */
            @JsonFormat(pattern = "yyyy-MM-dd")
            @Excel(name = "支付时间", width = 30, dateFormat = "yyyy-MM-dd")
    @Schema(description = "支付时间")
    private Date payTime;

    /** 取消时间 */
            @JsonFormat(pattern = "yyyy-MM-dd")
            @Excel(name = "取消时间", width = 30, dateFormat = "yyyy-MM-dd")
    @Schema(description = "取消时间")
    private Date cancelTime;

    /** 完成时间 */
            @JsonFormat(pattern = "yyyy-MM-dd")
            @Excel(name = "完成时间", width = 30, dateFormat = "yyyy-MM-dd")
    @Schema(description = "完成时间")
    private Date finishTime;

    /** 当前归属部门/学校 */
            @Excel(name = "当前归属部门/学校")
    @Schema(description = "当前归属部门/学校")
    private Long deptId;

/** 订单明细信息 */
@TableField(exist = false)
@Schema(description = "订单明细")
private List<XfptOrderItem> xfptOrderItemList;
@TableField(exist = false)
private String noticeTitle;
        }
