package com.xfgl.system.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xfgl.common.core.annotation.Excel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
public class FeedBackVo {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "$column.columnComment")
    private Long id;

    /**
     * 家长id
     */
    @Excel(name = "家长id")
    @Schema(description = "家长id")
    private String userId;

    /**
     * 家长姓名
     */
    @Excel(name = "家长姓名")
    @Schema(description = "家长姓名")
    private String userName;

    /**
     * 学校id
     */
    @Excel(name = "学校id")
    @Schema(description = "学校id")
    private String deptId;

    /**
     * 学校名称
     */
    @Excel(name = "学校名称")
    @Schema(description = "学校名称")
    private String deptName;

    /**
     * 反馈内容
     */
    @Excel(name = "反馈内容")
    @Schema(description = "反馈内容")
    private String feedbackContent;

    /**
     * 反馈附件
     */
    @Excel(name = "反馈附件")
    @Schema(description = "反馈附件")
    private String feedbackUrl;

    /**
     * 回复内容
     */
    @Excel(name = "回复内容")
    @Schema(description = "回复内容")
    private String replyContent;

    /**
     * 回复附件
     */
    @Excel(name = "回复附件")
    @Schema(description = "回复附件")
    private String replyUrl;

    @Excel(name = "回复状态")
    @Schema(description = "回复状态")
    private String replyState;

    @Excel(name = "回复人")
    @Schema(description = "回复人")
    private String replyBy;

    @Excel(name = "回复时间")
    @Schema(description = "回复时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date replyTime;

    /**
     * 是否公开 1 公开 0不公开
     *
     */
    @Excel(name = "是否公开")
    public String isPublic;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date createTime;
}
