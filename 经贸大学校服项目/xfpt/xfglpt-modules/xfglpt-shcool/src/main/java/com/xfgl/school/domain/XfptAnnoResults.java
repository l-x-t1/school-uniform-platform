package com.xfgl.school.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.experimental.Accessors;
import lombok.*;
import com.xfgl.common.core.annotation.Excel;
import com.xfgl.common.core.web.domain.BaseEntity;

import java.util.Date;
import java.util.List;

/**
 * 选用组织选购结果公示报备与公开对象 xfpt_anno_results
 *
 * @author yinet
 * @date 2025-04-23
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("xfpt_anno_results")
@Schema(description = "选用组织选购结果公示报备与公开")
public class XfptAnnoResults extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键id")
    private Long id;

    /**
     * 成交公告名称
     */
    @Excel(name = "成交公告名称")
    @Schema(description = "成交公告名称")
    private String noticeTitle;

    /**
     * 请选择采购公告信息名称
     */
    @Excel(name = "请选择采购公告信息名称")
    @Schema(description = "请选择采购公告信息名称")
    private String procurementNoticeName;

    /**
     * 请选择采购公告信息id
     */
    @Excel(name = "请选择采购公告信息id")
    @Schema(description = "请选择采购公告信息id")
    private String procurementNoticeId;

    /**
     * 成交企业名称
     */
    @Excel(name = "成交企业名称")
    @Schema(description = "成交企业名称")
    private String transactionCompanyName;

    /**
     * 中标价格
     */
    @Excel(name = "中标价格")
    @Schema(description = "中标价格")
    private String bidPrice;

    /**
     * 评委列表
     */
    @Excel(name = "评委列表")
    @Schema(description = "评委列表")
    private String judgesList;

    /**
     * 评委人数
     */
    @Excel(name = "评委人数")
    @Schema(description = "评委人数")
    private Long judgesCount;

    /**
     * 公示文件
     */
    @Excel(name = "公示文件")
    @Schema(description = "公示文件")
    private String announcementFile;

    /**
     * 其他文件
     */
    @Excel(name = "其他文件")
    @Schema(description = "其他文件")
    private String otherFile;


    /**
     * 部门id
     */
    @Excel(name = "部门id")
    @Schema(description = "部门id")
    private Long deptId;

    /**
     * 祖籍id
     */
    @Excel(name = "祖籍id")
    @Schema(description = "祖籍id")
    private String ancestors;

    /**
     * 班子会议id
     */
    @Schema(description = "班子会议id")
    private Long teamId;

    /**
     * 文件类型 1文档 2图片
     */
    private String fileType;

    /**
     * 是否发布
     */
    private String isRelease;

    /**
     * 选购模式
     */
    @TableField(exist = false)
    private String reportMode;

    /**
     * 采购时间
     */
    private Date cgsj;

    private String noticeFile;

    @TableField(exist = false)
    List<TeamMeetingSon> sonDataList;
}
