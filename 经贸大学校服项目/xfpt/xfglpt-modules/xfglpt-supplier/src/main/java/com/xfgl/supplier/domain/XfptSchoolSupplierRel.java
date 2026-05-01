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
 * 学校供应商合作关系对象 xfpt_school_supplier_rel
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
@TableName("xfpt_school_supplier_rel")
@Schema(description = "学校供应商合作关系")
public class XfptSchoolSupplierRel extends BaseEntity
        {
private static final long serialVersionUID = 1L;

    /** 主键 */
        @TableId(type= IdType.AUTO)
    @Schema(description = "主键")
    private Long id;

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

    /** 服务电话 */
            @Excel(name = "服务电话")
    @Schema(description = "服务电话")
    private String servicePhone;

    /** 统购公告ID */
            @Excel(name = "统购公告ID")
    @Schema(description = "统购公告ID")
    private Long noticeId;

    /** 合同ID */
            @Excel(name = "合同ID")
    @Schema(description = "合同ID")
    private Long contractId;

    /** 合作状态，1合作中 2已取消 3历史合作 4公告期内 */
            @Excel(name = "合作状态，1合作中 2已取消 3历史合作 4公告期内")
    @Schema(description = "合作状态，1合作中 2已取消 3历史合作 4公告期内")
    private String cooperateStatus;

    /** 合作开始时间 */
            @JsonFormat(pattern = "yyyy-MM-dd")
            @Excel(name = "合作开始时间", width = 30, dateFormat = "yyyy-MM-dd")
    @Schema(description = "合作开始时间")
    private Date startTime;

    /** 合作结束时间 */
            @JsonFormat(pattern = "yyyy-MM-dd")
            @Excel(name = "合作结束时间", width = 30, dateFormat = "yyyy-MM-dd")
    @Schema(description = "合作结束时间")
    private Date endTime;

    /** 取消原因 */
            @Excel(name = "取消原因")
    @Schema(description = "取消原因")
    private String cancelReason;

    /** 当前归属部门/学校 */
            @Excel(name = "当前归属部门/学校")
    @Schema(description = "当前归属部门/学校")
    private Long deptId;

    /** 组织祖级链路 */
            @Excel(name = "组织祖级链路")
    @Schema(description = "组织祖级链路")
    private String ancestors;

        }
