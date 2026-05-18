package com.xfgl.school.domain;

import java.math.BigDecimal;
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
 * 教育局结算管理对象 xfpt_edu_settlement
 *
 * @author codex
 * @date 2026-05-17
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

    /** 主键 */
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键")
    private Long id;

    /** 结算单位 */
    @Excel(name = "结算单位")
    @Schema(description = "结算单位")
    private String unitName;

    /** 项目名称 */
    @Excel(name = "项目名称")
    @Schema(description = "项目名称")
    private String projectName;

    /** 结算周期，格式 yyyy-MM */
    @Excel(name = "结算周期")
    @Schema(description = "结算周期，格式 yyyy-MM")
    private String settlementCycle;

    /** 结算状态，0待结算 1部分结算 2已结算 */
    @Excel(name = "结算状态，0待结算 1部分结算 2已结算")
    @Schema(description = "结算状态，0待结算 1部分结算 2已结算")
    private String status;

    /** 结算金额 */
    @Excel(name = "结算金额")
    @Schema(description = "结算金额")
    private BigDecimal settlementAmount;

    /** 税额 */
    @Excel(name = "税额")
    @Schema(description = "税额")
    private BigDecimal taxAmount;

    /** 已付金额 */
    @Excel(name = "已付金额")
    @Schema(description = "已付金额")
    private BigDecimal paidAmount;

    /** 未付金额 */
    @Excel(name = "未付金额")
    @Schema(description = "未付金额")
    private BigDecimal unpaidAmount;

    /** 当前归属部门/教育局 */
    @Excel(name = "当前归属部门/教育局")
    @Schema(description = "当前归属部门/教育局")
    private Long deptId;
}
