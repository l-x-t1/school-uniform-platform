package com.xfgl.school.controller;

import java.util.List;

import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.web.page.TableDataInfo;
import com.xfgl.school.domain.XfptSupplierSettleApply;
import com.xfgl.school.service.IXfptSupplierSettleApplyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 供应商入驻申请Controller
 */
@Tag(name = "供应商入驻申请")
@RestController
@RequestMapping("/supplierSettleApply")
public class SupplierSettleApplyController extends BaseController {

    @Autowired
    private IXfptSupplierSettleApplyService supplierSettleApplyService;

    @Operation(summary = "提交供应商入驻申请")
    @PostMapping
    public AjaxResult submit(@RequestBody XfptSupplierSettleApply apply) {
        supplierSettleApplyService.submitApply(apply);
        return AjaxResult.success("申请已提交，请等待教育局审核");
    }

    /* ---------- 教育局审核 ---------- */

    @Operation(summary = "查询待审核的入驻申请列表")
    @GetMapping("/audit/list")
    public TableDataInfo auditList() {
        startPage();
        List<XfptSupplierSettleApply> list = supplierSettleApplyService.lambdaQuery()
                .eq(XfptSupplierSettleApply::getAuditStatus, "0")
                .orderByDesc(XfptSupplierSettleApply::getCreateTime)
                .list();
        return getDataTable(list);
    }

    @Operation(summary = "审核通过入驻申请")
    @PutMapping("/audit/approve/{id}")
    public AjaxResult approve(@PathVariable Long id) {
        supplierSettleApplyService.approveApply(id);
        return AjaxResult.success("审核通过，已创建供应商账号和厂商信息");
    }

    @Operation(summary = "审核驳回入驻申请")
    @PutMapping("/audit/reject/{id}")
    public AjaxResult reject(@PathVariable Long id, @RequestParam(defaultValue = "") String remark) {
        supplierSettleApplyService.rejectApply(id, remark);
        return AjaxResult.success("已驳回该入驻申请");
    }
}