package com.xfgl.system.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.xfgl.common.core.annotation.Excel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SchoolInfoVo {
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
}
