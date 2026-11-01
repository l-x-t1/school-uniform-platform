package com.xfgl.school.domain.vo;

import com.xfgl.school.domain.SupplierMessage;
import com.xfgl.school.domain.XfptSupplierSettleApply;
import com.xfgl.system.api.domain.SysUser;
import com.xfgl.system.api.domain.XfptNotice;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "供应商入驻入口上下文")
public class SupplierSettleEntryContextVo {
    @Schema(description = "公告信息")
    private XfptNotice notice;

    @Schema(description = "当前登录用户")
    private SysUser loginUser;

    @Schema(description = "当前绑定供应商")
    private SupplierMessage supplier;

    @Schema(description = "最近一次申请")
    private XfptSupplierSettleApply latestApply;

    @Schema(description = "是否可提交")
    private boolean canSubmit;

    @Schema(description = "提示信息")
    private String message;
}
