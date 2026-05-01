package com.xfgl.school.domain;

import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.experimental.Accessors;
import lombok.*;
import com.xfgl.common.core.annotation.Excel;
import com.xfgl.common.core.web.domain.BaseEntity;

/**
 * 选用组织委托招标机构确定企业模式对象 xfpt_enterprise_mode
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
@TableName("xfpt_enterprise_mode")
@Schema(description = "选用组织委托招标机构确定企业模式")
public class XfptEnterpriseMode extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键id")
    private Long id;

    /**
     * 选购公告-公告名称
     */
    @Excel(name = "选购公告-公告名称")
    @Schema(description = "选购公告-公告名称")
    private String noticeName;

    /**
     * 选购公告-采购公告
     */
    @Excel(name = "选购公告-采购公告")
    @Schema(description = "选购公告-采购公告")
    private String noticeFile;
    /**
     * 文件类型
     * 1文件类型 2图片类型
     */
    @Excel(name = "文件类型")
    @Schema(description = "文件类型")
    private String fileType;

    /**
     * 选购公告-其他文件
     */
    @Excel(name = "选购公告-其他文件")
    @Schema(description = "选购公告-其他文件")
    private String otherFile;

    /**
     * 选购公告-是否提供样衣图
     */
    @Excel(name = "选购公告-是否提供样衣图")
    @Schema(description = "选购公告-是否提供样衣图")
    private String isSamplesPic;

    @Excel(name = "选购公告-样衣图")
    @Schema(description = "选购公告-样衣图")
    private String samplesPic;

    /**
     * 选购公告0  招标机构合作合同1  评估招标结果 2
     */
    @Excel(name = "选购公告0  招标机构合作合同1  评估招标结果 2")
    @Schema(description = "选购公告0  招标机构合作合同1  评估招标结果 2")
    private String type;

    /**
     * 招标机构合作合同-会议标题
     */
    @Excel(name = "招标机构合作合同-会议标题")
    @Schema(description = "招标机构合作合同-会议标题")
    private String meetingTitle;

    /**
     * 招标机构合作合同-招标公司
     */
    @Excel(name = "招标机构合作合同-招标公司")
    @Schema(description = "招标机构合作合同-招标公司")
    private String tenderingCompany;

    /**
     * 招标机构合作合同-招标代理合同签订时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "招标机构合作合同-招标代理合同签订时间", width = 30, dateFormat = "yyyy-MM-dd")
    @Schema(description = "招标机构合作合同-招标代理合同签订时间")
    private Date signingTime;

    /**
     * 招标机构合作合同-招标公司联系人姓名
     */
    @Excel(name = "招标机构合作合同-招标公司联系人姓名")
    @Schema(description = "招标机构合作合同-招标公司联系人姓名")
    private String contactsName;

    /**
     * 招标机构合作合同-招标公司联系方式
     */
    @Excel(name = "招标机构合作合同-招标公司联系方式")
    @Schema(description = "招标机构合作合同-招标公司联系方式")
    private String companyContactPhone;

    /**
     * 招标机构合作合同-招标公司联系地址
     */
    @Excel(name = "招标机构合作合同-招标公司联系地址")
    @Schema(description = "招标机构合作合同-招标公司联系地址")
    private String companyContactAddress;

    /**
     * 招标机构合作合同-执照
     */
    @Excel(name = "招标机构合作合同-执照")
    @Schema(description = "招标机构合作合同-执照")
    private String licenseFile;

    /**
     * 招标机构合作合同-招标代理合同
     */
    @Excel(name = "招标机构合作合同-招标代理合同")
    @Schema(description = "招标机构合作合同-招标代理合同")
    private String biddingAgencyContract;

    /**
     * 评估招标结果-评标结果会议标题
     */
    @Excel(name = "评估招标结果-评标结果会议标题")
    @Schema(description = "评估招标结果-评标结果会议标题")
    private String bidEvaluationTitle;

    /**
     * 评估招标结果-评标结果附件
     */
    @Excel(name = "评估招标结果-评标结果附件")
    @Schema(description = "评估招标结果-评标结果附件")
    private String bidEvaluationResults;


    /**
     * 是否发布 0撤回 1发布
     */
    @Excel(name = "是否发布 0未发布  1发布 2撤回")
    @Schema(description = "是否发布 0未发布  1发布 2撤回")
    private String isRelease;

    /**
     * 发布时间
     */
    @Schema(description = "发布时间")
    private Date releaseTime;

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
     * 教育局查看状态
     */
    @Schema(description = "教育局查看状态")
    private String lookStatus;

    /**
     * 供应商列表
     */
    @TableField(exist = false)
    private List<XfptSupplierList> supplierList;



}
