package com.xfgl.school.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.experimental.Accessors;
import lombok.*;
import com.xfgl.common.core.annotation.Excel;
import com.xfgl.common.core.web.domain.BaseEntity;

import java.util.Date;

/**
 * 采购汇总信息对象 xfpt_proc_summary
 *
 * @author yinet
 * @date 2025-05-09
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("xfpt_proc_summary")
@Schema(description = "采购汇总信息")
public class XfptProcSummary extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 标题
     */
    @Excel(name = "标题")
    @Schema(description = "标题")
    private String title;

    /**
     * 采购金额
     */
    @Excel(name = "采购金额")
    @Schema(description = "采购金额")
    private Long money;

    /**
     * 招标文件
     */
    @Excel(name = "招标文件")
    @Schema(description = "招标文件")
    private String inviteFile;

    /**
     * 中标文件
     */
    @Excel(name = "中标文件")
    @Schema(description = "中标文件")
    private String winBidFile;

    /**
     * 企业
     */
    @Excel(name = "企业")
    @Schema(description = "企业")
    private String enterprise;

    /**
     * 采购件数
     */
    @Excel(name = "采购件数")
    @Schema(description = "采购件数")
    private Long count;

    /**
     * 校服样式
     */
    @Excel(name = "校服样式")
    @Schema(description = "校服样式")
    private String schoolUniformFile;

    /**
     * 学校
     */
    @Excel(name = "学校")
    @Schema(description = "学校")
    private String schoolName;

    /**
     * 部门id
     */
    @Excel(name = "部门id")
    @Schema(description = "部门id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long deptId;

    /**
     * 祖籍id
     */
    @Excel(name = "祖籍id")
    @Schema(description = "祖籍id")
    private String ancestors;

    /**
     * 采购时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date buyTime;

    /**
     * 部门名称
     */
    @TableField(exist = false)
    private String deptName;

    /**
     * 学校类型
     */
    @TableField(exist = false)
    private String schoolType;

    /**
     * 班子会议id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long teamId;

    /**
     * 单价
     */
    private Double dj;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getMoney() {
        return money;
    }

    public void setMoney(Long money) {
        this.money = money;
    }

    public String getInviteFile() {
        return inviteFile;
    }

    public void setInviteFile(String inviteFile) {
        this.inviteFile = inviteFile;
    }

    public String getWinBidFile() {
        return winBidFile;
    }

    public void setWinBidFile(String winBidFile) {
        this.winBidFile = winBidFile;
    }

    public String getEnterprise() {
        return enterprise;
    }

    public void setEnterprise(String enterprise) {
        this.enterprise = enterprise;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public String getSchoolUniformFile() {
        return schoolUniformFile;
    }

    public void setSchoolUniformFile(String schoolUniformFile) {
        this.schoolUniformFile = schoolUniformFile;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public String getAncestors() {
        return ancestors;
    }

    public void setAncestors(String ancestors) {
        this.ancestors = ancestors;
    }

    public Date getBuyTime() {
        return buyTime;
    }

    public void setBuyTime(Date buyTime) {
        this.buyTime = buyTime;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getSchoolType() {
        return schoolType;
    }

    public void setSchoolType(String schoolType) {
        this.schoolType = schoolType;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public Double getDj() {
        return dj;
    }

    public void setDj(Double dj) {
        this.dj = dj;
    }

}
