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
 * 生产单对象 xfpt_production_order
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
@TableName("xfpt_production_order")
@Schema(description = "生产单")
public class XfptProductionOrder extends BaseEntity
        {
private static final long serialVersionUID = 1L;

    /** 主键 */
        @TableId(type= IdType.AUTO)
    @Schema(description = "主键")
    private Long id;

    /** 生产单号 */
            @Excel(name = "生产单号")
    @Schema(description = "生产单号")
    private String productionNo;

    /** 统购公告ID */
            @Excel(name = "统购公告ID")
    @Schema(description = "统购公告ID")
    private Long noticeId;

    /** 订单ID */
            @Excel(name = "订单ID")
    @Schema(description = "订单ID")
    private Long orderId;

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

    /** 状态，0待生产 1生产中 2生产完成 */
            @Excel(name = "状态，0待生产 1生产中 2生产完成")
    @Schema(description = "状态，0待生产 1生产中 2生产完成")
    private String status;

    /** 计划完成时间 */
            @JsonFormat(pattern = "yyyy-MM-dd")
            @Excel(name = "计划完成时间", width = 30, dateFormat = "yyyy-MM-dd")
    @Schema(description = "计划完成时间")
    private Date plannedFinishTime;

    /** 开始生产时间 */
            @JsonFormat(pattern = "yyyy-MM-dd")
            @Excel(name = "开始生产时间", width = 30, dateFormat = "yyyy-MM-dd")
    @Schema(description = "开始生产时间")
    private Date startTime;

    /** 生产完成时间 */
            @JsonFormat(pattern = "yyyy-MM-dd")
            @Excel(name = "生产完成时间", width = 30, dateFormat = "yyyy-MM-dd")
    @Schema(description = "生产完成时间")
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
