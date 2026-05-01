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
 * 选用组织名单报备与公开对象 xfpt_org_report_disclose
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
@TableName("xfpt_org_report_disclose")
@Schema(description = "选用组织名单报备与公开")
public class XfptOrgReportDisclose extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    @Schema(description = "$column.columnComment")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 班子会议主键id
     */
    @Excel(name = "班子会议主键id")
    @Schema(description = "班子会议主键id")
    private Long teamMeetingId;

    /**
     * 选用组织名单标题
     */
    @Excel(name = "选用组织名单标题")
    @Schema(description = "选用组织名单标题")
    private String title;


    /**
     * 总人数
     */
    @Excel(name = "总人数")
    @Schema(description = "总人数")
    private String total;

    /**
     * 家长学生代表总人数
     */
    @Excel(name = "家长学生代表总人数")
    @Schema(description = "家长学生代表总人数")
    private String jzxsDb;

    /**
     * 家长学生的总人数占比
     */
    @Excel(name = "家长学生的总人数占比")
    @Schema(description = "家长学生的总人数占比")
    private String jzxsZb;

    @TableField(exist = false)
    private List<XfptOrgReportDiscloseSon> sonDataList;

    /**
     * 部门id
     */
    @Schema(description = "部门id")
    private Long deptId;

    /**
     * 祖籍id
     */
    @Schema(description = "祖籍id")
    private String ancestors;

    /**
     * 附件
     */
    private String file;

}
