package com.xfgl.school.domain;
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
 * 供应商报名对象 xfpt_supplier_apply
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
@TableName("xfpt_supplier_apply")
@Schema(description = "供应商报名")
public class XfptSupplierApply extends BaseEntity
        {
private static final long serialVersionUID = 1L;

    /** 主键 */
        @TableId(type= IdType.AUTO)
    @Schema(description = "主键")
    private Long id;

    /** 公告ID */
    @Schema(description = "公告ID")
    private Long noticeId;

    /** 学校ID */
    @Schema(description = "学校ID")
    private Long schoolId;

    /** 供应商ID */
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

    /** 报名状态，0待提交 1已提交 2已撤回 */
            @Excel(name = "报名状态，0待提交 1已提交 2已撤回")
    @Schema(description = "报名状态，0待提交 1已提交 2已撤回")
    private String applyStatus;

    /** 资质审查状态，0待审查 1通过 2不通过 */
            @Excel(name = "资质审查状态，0待审查 1通过 2不通过")
    @Schema(description = "资质审查状态，0待审查 1通过 2不通过")
    private String qualificationStatus;

    /** 比选阶段状态，0未进入 1已进入 2已完成 */
            @Excel(name = "比选阶段状态，0未进入 1已进入 2已完成")
    @Schema(description = "比选阶段状态，0未进入 1已进入 2已完成")
    private String bidStageStatus;

    /** 比选结果状态，0未出结果 1已中选 2未中选 */
            @Excel(name = "比选结果状态，0未出结果 1已中选 2未中选")
    @Schema(description = "比选结果状态，0未出结果 1已中选 2未中选")
    private String bidResultStatus;

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
    @Schema(description = "报名附件JSON文本")
    private String applyFilesJson;

    /** 比选资料JSON文本 */
    @Schema(description = "比选资料JSON文本")
    private String compareFilesJson;

    /** 当前归属部门/学校 */
    @Schema(description = "当前归属部门/学校")
    private Long deptId;

    /** 公告标题 */
    @TableField(exist = false)
    @Excel(name = "公告标题")
    @Schema(description = "公告标题")
    private String noticeTitle;

    /** 公告编号 */
    @TableField(exist = false)
    @Excel(name = "公告编号")
    @Schema(description = "公告编号")
    private String noticeCode;

    /** 学校名称 */
    @TableField(exist = false)
    @Excel(name = "学校名称")
    @Schema(description = "学校名称")
    private String schoolName;

        }
