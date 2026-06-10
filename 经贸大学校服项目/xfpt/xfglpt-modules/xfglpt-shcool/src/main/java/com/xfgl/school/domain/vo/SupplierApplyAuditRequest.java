package com.xfgl.school.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "供应商报名审核参数")
public class SupplierApplyAuditRequest {
    @NotNull(message = "报名记录不能为空")
    @Schema(description = "报名ID")
    private Long applyId;

    @Schema(description = "审核意见")
    private String auditRemark;
}
