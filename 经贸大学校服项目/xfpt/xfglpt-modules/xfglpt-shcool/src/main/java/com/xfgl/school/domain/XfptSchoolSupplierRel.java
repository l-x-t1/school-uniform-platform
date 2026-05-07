package com.xfgl.school.domain;
import java.util.List;
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
 * @date 2026-05-04
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

    /** 服务电话 */
            @Excel(name = "服务电话")
    @Schema(description = "服务电话")
    private String servicePhone;

/** 学校供应商合作商品信息 */
@TableField(exist = false)
@Schema(description = "学校供应商合作商品")
private List<XfptSchoolSupplierGoodsRel> xfptSchoolSupplierGoodsRelList;
        }
