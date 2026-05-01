package com.xfgl.school.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.experimental.Accessors;
import lombok.*;
import com.xfgl.common.core.annotation.Excel;
import com.xfgl.common.core.web.domain.BaseEntity;

import java.util.List;

/**
 * 选用组织名单报备与公开从对象 xfpt_org_report_disclose_son
 *
 * @author wxm
 * @date 2025-04-23
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("xfpt_org_report_disclose_son")
@Schema(description = "选用组织名单报备与公开从")
public class XfptOrgReportDiscloseSon extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联表主键id
     */
    @Schema(description = "关联表主键id")
    private Long pId;

    /**
     * 职务
     */
    @Excel(name = "职务",sort = 1)
    @Schema(description = "职务")
    private String zw;


    /**
     * 代表类别
     */
    @Excel(name = "代表类别",sort = 2)
    @Schema(description = "代表类别")
    private String dblb;

    /**
     * 姓名
     */
    @Excel(name = "姓名",sort = 3)
    @Schema(description = "姓名")
    private String xm;

    /**
     * 手机号
     */
    @Excel(name = "手机号",sort = 4)
    @Schema(description = "手机号")
    private String phone;

    /**
     * 年级专业
     */
    @Excel(name = "年级/专业",sort = 5)
    @Schema(description = "年级专业")
    private String njZy;

    /**
     * 班级
     */
    @Excel(name = "班级",sort = 6)
    @Schema(description = "班级")
    private String bj;

    @TableField(exist = false)
    public List<Long> pidList;

}
