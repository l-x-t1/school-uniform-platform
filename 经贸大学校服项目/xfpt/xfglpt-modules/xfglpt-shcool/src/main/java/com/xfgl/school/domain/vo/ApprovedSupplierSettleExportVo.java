package com.xfgl.school.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xfgl.common.core.annotation.Excel;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import lombok.Data;

/**
 * 已审核通过供应商入驻导出对象
 */
@Data
@Schema(description = "已审核通过供应商入驻导出对象")
public class ApprovedSupplierSettleExportVo {

    @Excel(name = "厂商名称")
    @Schema(description = "厂商名称")
    private String supplierName;

    @Excel(name = "联系电话")
    @Schema(description = "联系电话")
    private String supplierPhone;

    @Excel(name = "入驻公告标题")
    @Schema(description = "入驻公告标题")
    private String noticeTitle;

    @Excel(name = "用户名")
    @Schema(description = "用户名")
    private String username;

    @Excel(name = "厂商类型")
    @Schema(description = "厂商类型")
    private String supplierType;

    @Excel(name = "法定代表人")
    @Schema(description = "法定代表人")
    private String legalRepresentative;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "审核时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "审核时间")
    private Date auditTime;

    @Excel(name = "审核人")
    @Schema(description = "审核人")
    private String auditBy;
}
