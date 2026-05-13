package com.xfgl.school.domain;

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
 * 学校信息对象 xfpt_school_message
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
@TableName("xfpt_school_message")
@Schema(description = "学校信息")
public class XfptSchoolMessage extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "$column.columnComment")
    private Long id;

    /**
     * 学校部门id
     */
    @Excel(name = "学校部门id")
    @Schema(description = "学校部门id")
    private Long deptId;

    /**
     * 学校名字
     */
    @Excel(name = "学校名字")
    @Schema(description = "学校名字")
    private String schoolName;

    /**
     * 院校类型
     */
    @Excel(name = "院校类型")
    @Schema(description = "院校类型")
    private String academyType;

    /**
     * 学校地区省
     */
    @Excel(name = "学校地区省")
    @Schema(description = "学校地区省")
    private String regionProvince;

    /**
     * 学校地区市
     */
    @Excel(name = "学校地区市")
    @Schema(description = "学校地区市")
    private String regionCity;

    /**
     * 学校地区县区
     */
    @Excel(name = "学校地区县区")
    @Schema(description = "学校地区县区")
    private String regionCounty;

    /**
     * 学校地址
     */
    @Excel(name = "学校地址")
    @Schema(description = "学校地址")
    private String detailedAddress;

    /**
     * 学校类别
     */
    @Excel(name = "学校类别")
    @Schema(description = "学校类别")
    private String schoolType;

    /**
     * 校徽
     */
    @Excel(name = "校徽")
    @Schema(description = "校徽")
    private String schoolBadge;
    /**
     * 校徽
     */
    @Excel(name = "学校区域")
    @Schema(description = "学校区域")
    private String schoolArea;
    @Excel(name = "负责人姓名")
    @Schema(description = "负责人姓名")
    private String principalName;

    @Excel(name = "负责人电话")
    @Schema(description = "负责人电话")
    private String principalPhone;

    @Excel(name = "银行账户户名")
    @Schema(description = "银行账户户名")
    private String bankAccountName;

    @Excel(name = "开户银行")
    @Schema(description = "开户银行")
    private String bankName;

    @Excel(name = "开户支行")
    @Schema(description = "开户支行")
    private String bankBranchName;

    @Excel(name = "银行卡号")
    @Schema(description = "银行卡号")
    private String bankAccountNo;

    //学校类型数量
    @TableField(exist = false)
    private int number;

    /**
     * 地区id
     */
    private String area;

    /**
     * 在校人数
     */
    private String peopleCount;

    public Long getId() {
        return id;
    }

    public Long getDeptId() {
        return deptId;
    }

    public String getSchoolName() {
        return schoolName;
    }

}
