package com.xfgl.school.domain;

import java.util.Date;
import java.util.List;

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
 * 模式一组织确认选购模式对象 xfpt_organization_mode
 *
 * @author yinet
 * @date 2025-05-08
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("xfpt_organization_mode")
@Schema(description = "模式一组织确认选购模式")
public class XfptOrganizationMode extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "$column.columnComment")
    private Long id;

    /**
     * 标题
     */
    @Excel(name = "标题")
    @Schema(description = "标题")
    private String title;

    /**
     * 是否提样衣
     */
    @Excel(name = "是否提样衣")
    @Schema(description = "是否提样衣")
    private String isSamplesPic;

    /**
     * 样衣图
     */
    @Schema(description = "样衣图")
    private String samplesPic;

    /**
     * 其他文件
     */
    @Schema(description = "其他文件")
    private String otherFile;

    /**
     * 公告文件
     */
    @Schema(description = "公告文件")
    private String noticeFile;

    /**
     * 采购公告模式选择
     */
    @Excel(name = "采购公告模式选择")
    @Schema(description = "采购公告模式选择")
    private String type;

    /**
     * 问卷id
     */
    @Schema(description = "问卷id")
    private Long quesId;

    /**
     * 报名截止时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "报名截止时间", width = 30, dateFormat = "yyyy-MM-dd")
    @Schema(description = "报名截止时间")
    private Date bmjzTime;

    /**
     * 报名上限
     */
    @Excel(name = "报名上限")
    @Schema(description = "报名上限")
    private Integer signupLimit;

    /**
     * 是否发布
     */
    @Excel(name = "是否发布")
    @Schema(description = "是否发布")
    private String isRelease;

    /**
     * 招标文件
     */
    @Schema(description = "招标文件")
    private String inviteFile;

    /**
     * 班子id
     */
    private Long teamId;

    private String ancestors;

    private Long deptId;

    private Date startTime;
    private Date endTime;

    /**
     * 供应商列表
     */
    @TableField(exist = false)
    private List<XfptSupplierList> supplierList;

}
