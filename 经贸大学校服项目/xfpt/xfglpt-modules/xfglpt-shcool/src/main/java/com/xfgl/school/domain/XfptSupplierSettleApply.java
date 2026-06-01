package com.xfgl.school.domain;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
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
 * 供应商入驻申请对象 xfpt_supplier_settle_apply
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("xfpt_supplier_settle_apply")
@Schema(description = "供应商入驻申请")
public class XfptSupplierSettleApply extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键")
    private Long id;

    @Excel(name = "用户名")
    @Schema(description = "用户名")
    private String username;

    @Excel(name = "手机号")
    @Schema(description = "手机号")
    private String phone;

    @Excel(name = "邮箱")
    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "密码哈希")
    private String passwordHash;

    @TableField(exist = false)
    @Schema(description = "密码")
    private String password;

    @Excel(name = "厂商名称")
    @Schema(description = "厂商名称")
    private String supplierName;

    @Excel(name = "联系电话")
    @Schema(description = "联系电话")
    private String supplierPhone;

    @Excel(name = "厂商类型")
    @Schema(description = "厂商类型")
    private String supplierType;

    @Excel(name = "法人代表")
    @Schema(description = "法人代表")
    private String legalRepresentative;

    @Excel(name = "职工人数")
    @Schema(description = "职工人数")
    private String staffNumber;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "成立时间", width = 30, dateFormat = "yyyy-MM-dd")
    @Schema(description = "成立时间")
    private Date establishedTime;

    @Excel(name = "厂商地址")
    @Schema(description = "厂商地址")
    private String supplierAddress;

    @Excel(name = "厂商简介")
    @Schema(description = "厂商简介")
    private String supplierIntroduce;

    @Schema(description = "营业执照/厂商资质")
    private String businessLicense;

    @Excel(name = "注册资金")
    @Schema(description = "注册资金")
    private String registeredCapital;

    @Schema(description = "信用评价")
    private String xypj;

    @Schema(description = "审核状态：0待审核，1通过，2驳回")
    private String auditStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "审核时间")
    private Date auditTime;

    @Schema(description = "审核备注")
    private String auditRemark;

    @Schema(description = "目标角色ID")
    private Long targetRoleId;

    @Schema(description = "目标角色标识")
    private String targetRoleKey;
}
