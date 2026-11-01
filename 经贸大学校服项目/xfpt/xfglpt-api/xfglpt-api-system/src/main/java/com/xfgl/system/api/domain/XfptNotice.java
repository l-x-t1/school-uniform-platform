package com.xfgl.system.api.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.experimental.Accessors;
import lombok.*;
import com.xfgl.common.core.annotation.Excel;
import com.xfgl.common.core.web.domain.BaseEntity;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 首页公告对象 xfpt_notice
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
@TableName("xfpt_notice")
@Schema(description = "首页公告")
public class XfptNotice extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 标题
     */
    @Excel(name = "标题")
    @Schema(description = "标题")
    private String title;

    /**
     * 内容
     */
    @Excel(name = "内容")
    @Schema(description = "内容")
    private String content;

    /**
     * 截止时间
     */
    @Excel(name = "截止时间", width = 20, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "截止时间")
    private java.util.Date endTime;


    /**
     * 类型 0采购公告  1成交公告
     */
    @Excel(name = "类型 0采购公告  1成交公告 2入围公告")
    @Schema(description = "类型 0采购公告  1成交公告")
    private String type;

    /**
     * 状态  0撤销  1发布
     */
    @Excel(name = "状态  0未发布  1发布 2撤回")
    @Schema(description = "状态 0未发布  1发布 2撤回")
    private String status;

    /**
     * 0组织选举  1家长选举 3第三方招标
     */
    @Excel(name = "0组织选举  1家长选举 3第三方招标")
    @Schema(description = "0组织选举  1家长选举 3第三方招标")
    private String xgType;

    @Schema(description = "文件地址")
    private String file;

    /**
     * 采购公告文件
     */
    @Schema(description = "采购/成交公告文件")
    private String ggFile;

    /**
     * 采购公告文件类型
     */
    @Schema(description = "文件类型")
    private String fileType;


    /**
     * 样衣图
     */
    @Schema(description = "样衣图")
    private String samplesPic;

    /**
     * 选购公告id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long enteId;

    /**
     * 阅读量
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long readCount;

    /**
     * 发布人id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    /**
     * 分类标识
     */
    private String catagory;

    /**
     * 封面图
     */
    private String fmImg;

    /**
     * 分类标识名称
     */
    @TableField(exist = false)
    private String catagoryName;

    /**
     * 地区
     */
    private String area;

    private String areaname;

    private String deptname;

    private String isBc;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long simId;

}
