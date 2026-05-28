package com.xfgl.school.domain;
import java.util.List;
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
 * 统购公告对象 xfpt_group_notice
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
@TableName("xfpt_group_notice")
@Schema(description = "统购公告")
public class XfptGroupNotice extends BaseEntity
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

    /** 公告标题 */
            @Excel(name = "公告标题")
    @Schema(description = "公告标题")
    private String title;

    /** 公告编号 */
            @Excel(name = "公告编号")
    @Schema(description = "公告编号")
    private String noticeCode;

    /** 公告类型，1选购公告 2结果公告 */
            @Excel(name = "公告类型，1选购公告 2结果公告")
    @Schema(description = "公告类型，1选购公告 2结果公告")
    private String noticeType;

    /** 公告正文 */
    @Schema(description = "公告正文")
    private String content;

    /** 年级ID集合 */
            @Excel(name = "年级ID集合")
    @Schema(description = "年级ID集合")
    private String gradeIds;

    /** 年级名称集合 */
            @Excel(name = "年级名称集合")
    @Schema(description = "年级名称集合")
    private String gradeNames;

    /** 班子会议ID */
            @Excel(name = "班子会议ID")
    @Schema(description = "班子会议ID")
    private Long teamMeetingId;

    /** 采购流程ID */
            @Excel(name = "采购流程ID")
    @Schema(description = "采购流程ID")
    private Long purchaseProcessId;

    /** 公告开始时间 */
            @JsonFormat(pattern = "yyyy-MM-dd")
            @Excel(name = "公告开始时间", width = 30, dateFormat = "yyyy-MM-dd")
    @Schema(description = "公告开始时间")
    private Date startTime;

    /** 公告截止时间 */
            @JsonFormat(pattern = "yyyy-MM-dd")
            @Excel(name = "公告截止时间", width = 30, dateFormat = "yyyy-MM-dd")
    @Schema(description = "公告截止时间")
    private Date endTime;

    /** 状态，0草稿 1已发布 2已结束 3已撤回 */
            @Excel(name = "状态，0草稿 1已发布 2已结束 3已撤回")
    @Schema(description = "状态，0草稿 1已发布 2已结束 3已撤回")
    private String status;

    /** 报名状态，0未开放 1报名中 2报名结束 */
            @Excel(name = "报名状态，0未开放 1报名中 2报名结束")
    @Schema(description = "报名状态，0未开放 1报名中 2报名结束")
    private String applyStatus;

    /** 发布时间 */
            @JsonFormat(pattern = "yyyy-MM-dd")
            @Excel(name = "发布时间", width = 30, dateFormat = "yyyy-MM-dd")
    @Schema(description = "发布时间")
    private Date publishTime;

    /** 结果发布时间 */
            @JsonFormat(pattern = "yyyy-MM-dd")
            @Excel(name = "结果发布时间", width = 30, dateFormat = "yyyy-MM-dd")
    @Schema(description = "结果发布时间")
    private Date resultPublishTime;

    /** 当前归属部门/学校 */
            @Excel(name = "当前归属部门/学校")
    @Schema(description = "当前归属部门/学校")
    private Long deptId;

/** 统购公告商品信息 */
@TableField(exist = false)
@Schema(description = "统购公告商品")
private List<XfptGroupNoticeItem> xfptGroupNoticeItemList;

/** Supplier visibility ids */
@TableField(exist = false)
@Schema(description = "Supplier visibility ids")
private List<Long> supplierIds;

/** Current supplier query id */
@TableField(exist = false)
@Schema(description = "Current supplier query id")
private Long supplierId;
        }
