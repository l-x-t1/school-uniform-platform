package com.xfgl.school.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.xfgl.common.core.annotation.Excel;
import com.xfgl.common.core.web.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 学校与供应商检验报告关联对象 xfpt_supplice_inspection_report
 *
 * @author yinet
 * @date 2025-04-22
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("xfpt_supplice_inspection_report")
@Schema(description = "学校与供应商检验报告关联")
public class XfptSuppliceInspectionReport extends BaseEntity {
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
    private String deptId;

    /**
     * 学校名字
     */
    @Excel(name = "学校名字")
    @Schema(description = "学校名字")
    private String deptName;

    /**
     * 厂商id
     */
    @Excel(name = "厂商id")
    @Schema(description = "厂商id")
    private String supplierId;

    /**
     * 厂商名字
     */
    @Excel(name = "厂商名字")
    @Schema(description = "厂商名字")
    private String supplierName;

    /**
     * 厂商联系电话
     */
    @Excel(name = "厂商联系电话")
    @Schema(description = "厂商联系电话")
    private String supplierPhone;

    /**
     * 班子会议id
     */
    @Excel(name = "班子会议id")
    @Schema(description = "班子会议id")
    private String teamMeetingId;

    /**
     * 部门祖级
     */
    private String ancestors;

    @TableField(exist = false)
    private String legalRepresentative;

    @TableField(exist = false)
    private String ggTitle;

    @TableField(exist = false)
    private String noticeName;

    @TableField(exist = false)
    private String manufacturerName;

    @TableField(exist = false)
    private String firstInspection;

    @TableField(exist = false)
    private String secondInspection;

    @TableField(exist = false)
    private String noticeStatus;

    @TableField(exist = false)
    private String noticeStatusText;

    @TableField(exist = false)
    private String dateRange;

    public String getNoticeName() {
        return noticeName;
    }

    public void setNoticeName(String noticeName) {
        this.noticeName = noticeName;
    }

    public String getManufacturerName() {
        return manufacturerName;
    }

    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    public String getFirstInspection() {
        return firstInspection;
    }

    public void setFirstInspection(String firstInspection) {
        this.firstInspection = firstInspection;
    }

    public String getSecondInspection() {
        return secondInspection;
    }

    public void setSecondInspection(String secondInspection) {
        this.secondInspection = secondInspection;
    }

    public String getNoticeStatus() {
        return noticeStatus;
    }

    public void setNoticeStatus(String noticeStatus) {
        this.noticeStatus = noticeStatus;
    }

    public String getNoticeStatusText() {
        return noticeStatusText;
    }

    public void setNoticeStatusText(String noticeStatusText) {
        this.noticeStatusText = noticeStatusText;
    }

    public String getDateRange() {
        return dateRange;
    }

    public void setDateRange(String dateRange) {
        this.dateRange = dateRange;
    }

}
