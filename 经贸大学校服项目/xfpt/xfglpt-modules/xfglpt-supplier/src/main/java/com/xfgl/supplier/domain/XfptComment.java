package com.xfgl.supplier.domain;

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
 * 商品评价对象 xfpt_comment
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
@TableName("xfpt_comment")
@Schema(description = "商品评价")
public class XfptComment extends BaseEntity
        {
private static final long serialVersionUID = 1L;

    /** 主键 */
        @TableId(type= IdType.AUTO)
    @Schema(description = "主键")
    private Long id;

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

    /** 商品ID */
            @Excel(name = "商品ID")
    @Schema(description = "商品ID")
    private Long goodsId;

    /** 商品名称快照 */
            @Excel(name = "商品名称快照")
    @Schema(description = "商品名称快照")
    private String goodsName;

    /** 评价人用户ID */
            @Excel(name = "评价人用户ID")
    @Schema(description = "评价人用户ID")
    private Long userId;

    /** 评价人姓名 */
            @Excel(name = "评价人姓名")
    @Schema(description = "评价人姓名")
    private String userName;

    /** 供应商ID */
            @Excel(name = "供应商ID")
    @Schema(description = "供应商ID")
    private Long supplierId;

    /** 评分 */
            @Excel(name = "评分")
    @Schema(description = "评分")
    private Integer score;

    /** 评价内容 */
            @Excel(name = "评价内容")
    @Schema(description = "评价内容")
    private String content;

    /** 评价图片JSON文本 */
            @Excel(name = "评价图片JSON文本")
    @Schema(description = "评价图片JSON文本")
    private String imagesJson;

    /** 供应商回复内容 */
            @Excel(name = "供应商回复内容")
    @Schema(description = "供应商回复内容")
    private String replyContent;

    /** 回复时间 */
            @JsonFormat(pattern = "yyyy-MM-dd")
            @Excel(name = "回复时间", width = 30, dateFormat = "yyyy-MM-dd")
    @Schema(description = "回复时间")
    private Date replyTime;

    /** 状态，0正常 1隐藏 */
            @Excel(name = "状态，0正常 1隐藏")
    @Schema(description = "状态，0正常 1隐藏")
    private String status;

        }
