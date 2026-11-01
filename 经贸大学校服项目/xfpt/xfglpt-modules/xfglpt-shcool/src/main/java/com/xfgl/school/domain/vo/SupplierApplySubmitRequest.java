package com.xfgl.school.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "供应商报名提交参数")
public class SupplierApplySubmitRequest {
    @NotNull(message = "采购公告不能为空")
    @Schema(description = "采购公告ID")
    private Long noticeId;

    @Schema(description = "生产许可附件")
    private String productionLicenseUrls;

    @Schema(description = "质检报告附件")
    private String qualityReportUrls;

    @Schema(description = "样衣图片")
    private String sampleImageUrls;

    @Schema(description = "补充附件")
    private String attachmentUrls;

    @Schema(description = "备注")
    private String remark;
}
