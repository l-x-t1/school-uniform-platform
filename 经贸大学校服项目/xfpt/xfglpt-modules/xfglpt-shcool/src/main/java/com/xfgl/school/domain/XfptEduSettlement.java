package com.xfgl.school.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
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
 * 教育局结算管理对象。
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("xfpt_edu_settlement")
@Schema(description = "教育局结算管理")
public class XfptEduSettlement extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键")
    private Long id;

    @Excel(name = "结算单位")
    @Schema(description = "结算单位")
    private String unitName;

    @Excel(name = "项目名称")
    @Schema(description = "项目名称")
    private String projectName;

    @Excel(name = "结算周期")
    @Schema(description = "结算周期，格式 yyyy-MM")
    private String settlementCycle;

    @Excel(name = "结算状态")
    @Schema(description = "结算状态，0待结算 1部分结算 2已结算")
    private String status;

    @Excel(name = "结算金额")
    @Schema(description = "结算金额")
    private BigDecimal settlementAmount;

    @Schema(description = "提现单号")
    private String withdrawNo;

    @Schema(description = "供应商名称")
    private String supplierName;

    @Schema(description = "供应商ID")
    private Long supplierId;

    @Schema(description = "申请金额")
    private BigDecimal applyAmount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "申请时间")
    private Date applyTime;

    @Excel(name = "税额")
    @Schema(description = "税额")
    private BigDecimal taxAmount;

    @Excel(name = "已付金额")
    @Schema(description = "已付金额")
    private BigDecimal paidAmount;

    @Excel(name = "未付金额")
    @Schema(description = "未付金额")
    private BigDecimal unpaidAmount;

    @Excel(name = "部门ID")
    @Schema(description = "当前归属部门/教育局")
    private Long deptId;
}
