package com.xfgl.school.domain;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
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
 * 家长购买意见征集对象 xfpt_jz_buy_opinions
 *
 * @author wxm
 * @date 2025-04-25
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("xfpt_jz_buy_opinions")
@Schema(description = "家长购买意见征集")
public class XfptJzBuyOpinions extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Schema(description = "主键")
    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 班子会议id
     */
    @Excel(name = "班子会议id")
    @Schema(description = "班子会议id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long teamMeetingId;

    /**
     * 意见征集标题
     */
    @Excel(name = "意见征集标题")
    @Schema(description = "意见征集标题")
    private String title;

    /**
     * 意见征集类型 参考字典 sc_yjzhlx
     */
    @Excel(name = "意见征集类型")
    @Schema(description = "意见征集类型")
    private String zjlx;

    /**
     * 选择日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "选择日期", width = 30, dateFormat = "yyyy-MM-dd")
    @Schema(description = "选择日期")
    private Date xzrq;

    /**
     * 意见征集情况
     */
    @Excel(name = "意见征集情况")
    @Schema(description = "意见征集情况")
    private String yjzjqk;

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
     * 姓名
     */
    @Excel(name = "姓名")
    @Schema(description = "姓名")
    private String xm;

    /**
     * 投票起止时间
     */
    @Excel(name = "投票起止时间")
    @Schema(description = "投票起止时间")
    private String tpqzsj;

    /**
     * 投票人需填写信息
     */
    @Excel(name = "投票人需填写信息")
    @Schema(description = "投票人需填写信息")
    private String txxx;

    /**
     * 供应企业服务年限（拟）
     */
    private String fwnx;

    /**
     * 模板配置id
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long configId;

    /**
     * 模板内容
     */
    private String configContent;

    /**
     * 线下附件
     */
    private String fj;

    @TableField(exist = false)
    private List<XfptJzBuyOpinionsSon> sonDataList;

    /**
     * 年级采购子表
     */
    @TableField(exist = false)
    private List<XfptBuyGradeClass> gradeClassList;

    /**
     * 部门id
     */
    @Schema(description = "部门id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long deptId;

    /**
     * 祖籍id
     */
    @Schema(description = "祖籍id")
    private String ancestors;

    /**
     * 问卷模板id,仅记录使用的哪个模板
     */
    private Long questionConfigId;

    /**
     *  关联的班子会议问卷id - 绑定业务
     */
    private Long questionBusinessId;

    /**
     * 人员名单
     */
    private String names;

    /**
     * 投票人数
     */
    private Integer count;

    @TableField(exist = false)
    private Integer votedCount;
    /**
     * 投票率
     */
    @TableField(exist = false)
    private double votingRate;

    @TableField(exist = false)
    private List<XfptQuestionnaireSurvey> sonList;
}
