package com.xfgl.school.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xfgl.common.core.annotation.Excel;
import com.xfgl.common.core.web.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 商品评论回复对象 xfpt_comment_reply
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
@TableName("xfpt_comment_reply")
@Schema(description = "商品评论回复")
public class XfptCommentReply extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键")
    private Long id;

    /** 所属评论ID */
    @Excel(name = "所属评论ID")
    @Schema(description = "所属评论ID")
    private Long commentId;

    /** 订单ID */
    @Excel(name = "订单ID")
    @Schema(description = "订单ID")
    private Long orderId;

    /** 订单明细ID */
    @Excel(name = "订单明细ID")
    @Schema(description = "订单明细ID")
    private Long orderItemId;

    /** 商品ID */
    @Excel(name = "商品ID")
    @Schema(description = "商品ID")
    private Long goodsId;

    /** 供应商ID */
    @Excel(name = "供应商ID")
    @Schema(description = "供应商ID")
    private Long supplierId;

    /** 回复人ID */
    @Excel(name = "回复人ID")
    @Schema(description = "回复人ID")
    private Long fromUserId;

    /** 回复人名称快照 */
    @Excel(name = "回复人名称快照")
    @Schema(description = "回复人名称快照")
    private String fromUserName;

    /** 回复人角色，1家长 2供应商 */
    @Excel(name = "回复人角色")
    @Schema(description = "回复人角色，1家长 2供应商")
    private String fromRoleType;

    /** 回复人角色标题快照 */
    @Excel(name = "回复人角色标题快照")
    @Schema(description = "回复人角色标题快照")
    private String fromRoleTitle;

    /** 被回复人ID */
    @Excel(name = "被回复人ID")
    @Schema(description = "被回复人ID")
    private Long toUserId;

    /** 被回复人名称快照 */
    @Excel(name = "被回复人名称快照")
    @Schema(description = "被回复人名称快照")
    private String toUserName;

    /** 被回复人角色，1家长 2供应商 */
    @Excel(name = "被回复人角色")
    @Schema(description = "被回复人角色，1家长 2供应商")
    private String toRoleType;

    /** 被回复人角色标题快照 */
    @Excel(name = "被回复人角色标题快照")
    @Schema(description = "被回复人角色标题快照")
    private String toRoleTitle;

    /** 回复内容 */
    @Schema(description = "回复内容")
    private String content;

    /** 状态，0正常 1隐藏 */
    @Excel(name = "状态", readConverterExp = "0=正常,1=隐藏")
    @Schema(description = "状态，0正常 1隐藏")
    private String status;

    /** 排序值 */
    @Excel(name = "排序值")
    @Schema(description = "排序值")
    private Integer sortNum;
}
