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
 * 一校为主模式（模式二）对象 xfpt_yxwf_mode
 *
 * @author yinet
 * @date 2025-05-23
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("xfpt_yxwf_mode")
@Schema(description = "一校为主模式（模式二）")
public class XfptYxwfMode extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @TableId(type= IdType.AUTO)
    @Schema(description = "$column.columnComment")
    private Long id;

    /** 标题 */
    @Excel(name = "标题")
    @Schema(description = "标题")
    private String title;

    /** 是否提供样衣图 */
    @Excel(name = "是否提供样衣图")
    @Schema(description = "是否提供样衣图")
    private String isSamplesPic;

    /** 样衣图 */
    @Excel(name = "样衣图")
    @Schema(description = "样衣图")
    private String samplesPic;

    /** 其他文件 */
    @Excel(name = "采购公告")
    @Schema(description = "采购公告")
    private String noticeFile;
    /** 其他文件 */
    @Excel(name = "其他文件")
    @Schema(description = "其他文件")
    private String otherFile;

    /** 报名截止时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "报名截止时间", width = 30, dateFormat = "yyyy-MM-dd")
    @Schema(description = "报名截止时间")
    private Date bmjzTime;

    /** 是否发布 */
    @Excel(name = "是否发布")
    @Schema(description = "是否发布")
    private String isRelease;

    /** 班子会议id */
    @Excel(name = "班子会议id")
    @Schema(description = "班子会议id")
    private Long teamId;

    /** 部门id */
    @Excel(name = "部门id")
    @Schema(description = "部门id")
    private Long deptId;

    /** 祖籍id */
    @Excel(name = "祖籍id")
    @Schema(description = "祖籍id")
    private String ancestors;

    /** 选用企业模式 0代理招标 1组织确定 */
    @Excel(name = "选用企业模式 0代理招标 1组织确定")
    @Schema(description = "选用企业模式 0代理招标 1组织确定")
    private String modeType;


    /** 联合学校名单 */
    @Excel(name = "联合学校名单")
    @Schema(description = "联合学校名单")
    private String schoolName;

    /** 联合学校名单 */
    @Excel(name = "发布时间")
    @Schema(description = "发布时间")
    private Date releaseTime;
    /** 类型 0比选  1询价  0其他 */
    @Excel(name = "类型 0比选  1询价  0其他")
    @Schema(description = "类型 0比选  1询价  0其他")
    private String type;
    /**
     * 供应商列表
     */
    @TableField(exist = false)
    private List<XfptSupplierList> supplierList;

}
