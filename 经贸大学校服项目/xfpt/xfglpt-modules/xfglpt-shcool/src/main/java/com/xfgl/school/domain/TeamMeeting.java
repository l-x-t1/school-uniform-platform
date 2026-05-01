package com.xfgl.school.domain;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.experimental.Accessors;
import lombok.*;
import com.xfgl.common.core.annotation.Excel;
import com.xfgl.common.core.web.domain.BaseEntity;
import nonapi.io.github.classgraph.json.Id;

/**
 * 班子会议对象 xfpt_team_meeting
 *
 * @author wxm
 * @date 2025-04-22
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("xfpt_team_meeting")
@Schema(description = "班子会议")
public class TeamMeeting extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    @Id
    private Long id;

    /**
     * 会议标题
     */
    @Excel(name = "会议标题")
    @Schema(description = "会议标题")
    private String title;

    /**
     * 会议时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "会议时间", width = 30, dateFormat = "yyyy-MM-dd")
    @Schema(description = "会议时间")
    private Date meetingTime;

    /**
     * 参会人员
     */
    @Excel(name = "参会人员")
    @Schema(description = "参会人员")
    private String meetingUser;

    /**
     * 职务
     */
    @Excel(name = "职务")
    @Schema(description = "职务")
    private String zw;

    /**
     * 代表类别
     */
    @Excel(name = "代表类别")
    @Schema(description = "代表类别")
    private String dblb;

    /**
     * 组织组长姓名
     */
    @Excel(name = "组织组长姓名")
    @Schema(description = "组织组长姓名")
    private String zzName;

    /**
     * 会议纪要
     */
    @Excel(name = "会议纪要")
    @Schema(description = "会议纪要")
    private String hyjy;

    /**
     * 当前对应步骤
     */
    @Excel(name = "当前对应步骤")
    @Schema(description = "当前对应步骤")
    private Long currOrder;

    /**
     * 时间轴记录
     * 命名规则：[模块名称#时间,...]
     */
    @Excel(name = "时间轴记录")
    @Schema(description = "时间轴记录")
    private String timeTb;

    /**
     * 供应服务年限
     */
    @Excel(name = "供应服务年限")
    @Schema(description = "供应服务年限")
    private String gyfwnx;

    /**
     * 教育局查看状态 0：未查看（默认）、1：已查看
     */
    private Integer jyjStatus;

    /**
     * 从表选购选购需求
     */
    @TableField(exist = false)
    private List<TeamMeetingSon> teamMeetingSonList;

    @TableField(exist = false)
    private String sonJsonData;

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
     * 时间轴列表数据
     */
    @TableField(exist = false)
    private List<String> timeAxisList;

    private Long teamId;


}
