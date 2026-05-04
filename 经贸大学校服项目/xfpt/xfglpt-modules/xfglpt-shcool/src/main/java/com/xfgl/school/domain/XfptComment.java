package com.xfgl.school.domain;
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
 * 商品评价对象 xfpt_comment
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
@TableName("xfpt_comment")
@Schema(description = "商品评价")
public class XfptComment extends BaseEntity
        {
private static final long serialVersionUID = 1L;

    /** 主键 */
        @TableId(type= IdType.AUTO)
    @Schema(description = "主键")
    private Long id;

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

    /** 商品ID */
            @Excel(name = "商品ID")
    @Schema(description = "商品ID")
    private Long goodsId;

    /** 商品名称快照 */
            @Excel(name = "商品名称快照")
    @Schema(description = "商品名称快照")
    private String goodsName;

    /** 评价用户ID */
            @Excel(name = "评价用户ID")
    @Schema(description = "评价用户ID")
    private Long userId;

    /** 评价用户名快照 */
            @Excel(name = "评价用户名快照")
    @Schema(description = "评价用户名快照")
    private String userName;

    /** 供应商ID */
            @Excel(name = "供应商ID")
    @Schema(description = "供应商ID")
    private Long supplierId;

    /** 评分，1-5分 */
            @Excel(name = "评分，1-5分")
    @Schema(description = "评分，1-5分")
    private Integer score;

    /** 评价内容 */
    @Schema(description = "评价内容")
    private String content;

    /** 评价图片JSON文本 */
    @Schema(description = "评价图片JSON文本")
    private String imagesJson;

    /** 历史商家回复内容，后续回复使用xfpt_comment_reply */
    @Schema(description = "历史商家回复内容，后续回复使用xfpt_comment_reply")
    private String replyContent;

    /** 历史商家回复时间 */
            @JsonFormat(pattern = "yyyy-MM-dd")
            @Excel(name = "历史商家回复时间", width = 30, dateFormat = "yyyy-MM-dd")
    @Schema(description = "历史商家回复时间")
    private Date replyTime;

    /** 评价状态，0正常 1隐藏 */
            @Excel(name = "评价状态，0正常 1隐藏")
    @Schema(description = "评价状态，0正常 1隐藏")
    private String status;

/** 商品评论回复信息 */
@TableField(exist = false)
@Schema(description = "商品评论回复")
private List<XfptCommentReply> xfptCommentReplyList;
        }
