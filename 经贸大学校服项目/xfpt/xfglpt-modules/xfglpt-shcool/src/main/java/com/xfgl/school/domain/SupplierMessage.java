package com.xfgl.school.domain;

import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.experimental.Accessors;
import lombok.*;
import com.xfgl.common.core.annotation.Excel;
import com.xfgl.common.core.web.domain.BaseEntity;

/**
 * 供应商信息对象 supplier_message
 *
 * @author yinet
 * @date 2025-04-22
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("xfpt_supplier_message")
@Schema(description = "供应商信息")
public class SupplierMessage extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "$column.columnComment")
    private Long id;

    /**
     * 厂商名称
     */
    @Excel(name = "厂商名称")
    @Schema(description = "厂商名称")
    private String supplierName;

    /**
     * 联系电话
     */
    @Excel(name = "联系电话")
    @Schema(description = "联系电话")
    private String supplierPhone;

    /**
     * 厂商类型
     */
    @Excel(name = "厂商类型")
    @Schema(description = "厂商类型")
    private String supplierType;

    /**
     * 法人代表
     */
    @Excel(name = "法人代表")
    @Schema(description = "法人代表")
    private String legalRepresentative;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @Schema(description = "更新时间")
    private Date turnoverTime;

    /**
     * 职工人数
     */
    @Excel(name = "职工人数")
    @Schema(description = "职工人数")
    private String staffNumber;

    /**
     * 成立时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "成立时间", width = 30, dateFormat = "yyyy-MM-dd")
    @Schema(description = "成立时间")
    private Date establishedTime;

    /**
     * 厂商地址
     */
    @Excel(name = "厂商地址")
    @Schema(description = "厂商地址")
    private String supplierAddress;

    /**
     * 厂商简介
     */
    @Excel(name = "厂商简介")
    @Schema(description = "厂商简介")
    private String supplierIntroduce;

    /**
     * 设备条件
     */
    @Excel(name = "设备条件")
    @Schema(description = "设备条件")
    private String equipmentConditions;

    /**
     * 营业执照
     */
//    @Excel(name = "营业执照")
    @Schema(description = "营业执照")
    private String businessLicense;

    /**
     * 营业执照
     */
    @Excel(name = "注册资金")
    @Schema(description = "注册资金")
    private String registeredCapital;

    @Excel(name = "参保人数")
    @Schema(description = "参保人数")
    private String insuredNumber;

    @Schema(description = "1在黑名单")
    private String blacklistStatus;

    //服务学校数量
    @Excel(name = "服务学校数量")
    @TableField(exist = false)
    private String serveSchoolNumber;
    //服务学校数量
    @Excel(name = "部门")
    private String deptId;
    //服务学校数量
    @Excel(name = "部门名称")
    private String deptName;
    /**
     * 部门祖级
     */
    private String ancestors;

    /**
     * 信用评价
     */
    private String xypj;

    /**
     * 违规原因
     */
    private String violationReason;

}
