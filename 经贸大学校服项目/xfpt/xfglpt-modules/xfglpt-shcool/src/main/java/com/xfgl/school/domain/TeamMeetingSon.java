package com.xfgl.school.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.experimental.Accessors;
import lombok.*;
import com.xfgl.common.core.annotation.Excel;
import com.xfgl.common.core.web.domain.BaseEntity;

import java.util.List;

/**
 * 选购需求对象 team_meeting_son
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
@TableName("xfpt_team_meeting_son")
@Schema(description = "选购需求")
public class TeamMeetingSon extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private Long id;

    /**
     * 班子会议主键
     */
    @Excel(name = "班子会议主键")
    @Schema(description = "班子会议主键")
    private Long teamMeetingId;

    /**
     * 采购年级
     */
    @Excel(name = "采购年级")
    @Schema(description = "采购年级")
    private String cgnj;

    /**
     * 采购年级id
     */
    private Long cgnjId;

    /**
     * 消费品类
     */
    @Excel(name = "消费品类")
    @Schema(description = "消费品类")
    private String xfpl;

    /**
     * 每个学生尼购买套数
     */
    @Excel(name = "每个学生尼购买套数")
    @Schema(description = "每个学生尼购买套数")
    private String gmts;

    /**
     * 每套价格区间
     */
    @Excel(name = "每套价格区间")
    @Schema(description = "每套价格区间")
    private String jgqj;

    /**
     * 备注
     */
    @Excel(name = "备注")
    @Schema(description = "备注")
    private String bz;


    @TableField(exist = false)
    private List<Long> teamMeetingIdList;

}
