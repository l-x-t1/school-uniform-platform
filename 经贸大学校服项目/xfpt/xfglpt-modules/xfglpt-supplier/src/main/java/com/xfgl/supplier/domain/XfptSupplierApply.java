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
 * 企业报名及资格审查对象 xfpt_supplier_apply
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
@TableName("xfpt_supplier_apply")
@Schema(description = "企业报名及资格审查")
public class XfptSupplierApply extends BaseEntity
        {
private static final long serialVersionUID = 1L;

    /** 主键 */
        @TableId(type= IdType.AUTO)
    @Schema(description = "主键")
    private Long id;

    /** 公告ID */
            @Excel(name = "公告ID")
    @Schema(description = "公告ID")
    private Long noticeId;

    /** 学校ID */
            @Excel(name = "学校ID")
    @Schema(description = "学校ID")
    private Long schoolId;

    /** 供应商ID */
            @Excel(name = "供应商ID")
    @Schema(description = "供应商ID")
    private Long supplierId;

    /** 供应商名称快照 */
            @Excel(name = "供应商名称快照")
    @Schema(description = "供应商名称快照")
    private String supplierName;

    /** 报名单号 */
            @Excel(name = "报名单号")
    @Schema(description = "报名单号")
    private String applyNo;

    /** 报名状态（0待提交 1已提交 2已撤回） */
            @Excel(name = "报名状态", readConverterExp = "0=待提交,1=已提交,2=已撤回")
    @Schema(description = "报名状态（0待提交 1已提交 2已撤回）")
    private String applyStatus;

    /** 资格审查状态（0待审查 1通过 2不通过） */
            @Excel(name = "资格审查状态", readConverterExp = "0=待审查,1=通过,2=不通过")
    @Schema(description = "资格审查状态（0待审查 1通过 2不通过）")
    private String qualificationStatus;

    /** 比选阶段状态（0未参与 1已参与 2已中选 3未中选） */
            @Excel(name = "比选阶段状态", readConverterExp = "0=未参与,1=已参与,2=已中选,3=未中选")
    @Schema(description = "比选阶段状态（0未参与 1已参与 2已中选 3未中选）")
    private String bidStageStatus;

    /** 报名时间 */
            @JsonFormat(pattern = "yyyy-MM-dd")
            @Excel(name = "报名时间", width = 30, dateFormat = "yyyy-MM-dd")
    @Schema(description = "报名时间")
    private Date applyTime;

    /** 审核时间 */
            @JsonFormat(pattern = "yyyy-MM-dd")
            @Excel(name = "审核时间", width = 30, dateFormat = "yyyy-MM-dd")
    @Schema(description = "审核时间")
    private Date auditTime;

    /** 审核意见 */
            @Excel(name = "审核意见")
    @Schema(description = "审核意见")
    private String auditRemark;

    /** 报名附件JSON文本 */
            @Excel(name = "报名附件JSON文本")
    @Schema(description = "报名附件JSON文本")
    private String applyFilesJson;

    /** 比选附件JSON文本 */
            @Excel(name = "比选附件JSON文本")
    @Schema(description = "比选附件JSON文本")
    private String compareFilesJson;

    /** 部门ID */
            @Excel(name = "部门ID")
    @Schema(description = "部门ID")
    private Long deptId;

    /** 祖级列表 */
            @Excel(name = "祖级列表")
    @Schema(description = "祖级列表")
    private String ancestors;

        }
