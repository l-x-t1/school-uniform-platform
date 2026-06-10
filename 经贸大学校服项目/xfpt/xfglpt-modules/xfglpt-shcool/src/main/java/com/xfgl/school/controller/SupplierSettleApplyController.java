package com.xfgl.school.controller;

import com.xfgl.common.core.utils.StringUtils;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.web.page.TableDataInfo;
import com.xfgl.common.core.web.page.PageDomain;
import com.xfgl.common.core.web.page.TableSupport;
import com.xfgl.common.log.annotation.Log;
import com.xfgl.common.log.enums.BusinessType;
import com.xfgl.school.domain.XfptSupplierSettleApply;
import com.xfgl.school.domain.vo.ApprovedSupplierSettleExportVo;
import com.xfgl.school.domain.vo.SupplierSettleEntryContextVo;
import com.xfgl.school.service.IXfptSupplierSettleApplyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
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

    @Operation(summary = "公开查询供应商入驻入口上下文")
    @GetMapping("/public/entryContext")
    public AjaxResult publicEntryContext(@RequestParam Long noticeId) {
        SupplierSettleEntryContextVo context = supplierSettleApplyService.getPublicEntryContext(noticeId);
        return AjaxResult.success(context);
    }

    @Operation(summary = "登录后查询供应商入驻入口上下文")
    @GetMapping("/entryContext")
    public AjaxResult entryContext(@RequestParam Long noticeId) {
        SupplierSettleEntryContextVo context = supplierSettleApplyService.getLoginEntryContext(noticeId);
        return AjaxResult.success(context);
    }

    @Operation(summary = "公开提交供应商注册入驻申请")
    @PostMapping("/public/register")
    public AjaxResult publicRegister(@RequestBody XfptSupplierSettleApply apply) {
        supplierSettleApplyService.submitRegisterApply(apply);
        return AjaxResult.success("申请已提交，请等待教育局审核");
    }

    @Operation(summary = "登录后提交供应商入驻申请")
    @PostMapping("/loginBound")
    public AjaxResult loginBoundSubmit(@RequestBody XfptSupplierSettleApply apply) {
        supplierSettleApplyService.submitLoginBoundApply(apply);
        return AjaxResult.success("申请已提交，请等待教育局审核");
    }

    @Operation(summary = "校验账号并查询绑定厂商信息")
    @PostMapping("/checkBinding")
    public AjaxResult checkBinding(@RequestBody XfptSupplierSettleApply apply) {
        SupplierSettleEntryContextVo context = supplierSettleApplyService.checkBinding(
                apply.getUsername(), apply.getPassword());
        return AjaxResult.success(context);
    }

    /* ---------- 教育局审核 ---------- */

    @Operation(summary = "查询待审核的入驻申请列表")
    @GetMapping("/audit/list")
    public TableDataInfo auditList(XfptSupplierSettleApply query) {
        List<XfptSupplierSettleApply> list = supplierSettleApplyService.listPendingByPublisher(query);
        PageDomain pageDomain = TableSupport.buildPageRequest();
        int pageNum = pageDomain.getPageNum() == null || pageDomain.getPageNum() < 1 ? 1 : pageDomain.getPageNum();
        int pageSize = pageDomain.getPageSize() == null || pageDomain.getPageSize() < 1 ? 10 : pageDomain.getPageSize();
        int fromIndex = Math.max((pageNum - 1) * pageSize, 0);
        List<XfptSupplierSettleApply> pageRows = fromIndex >= list.size()
                ? Collections.emptyList()
                : list.subList(fromIndex, Math.min(fromIndex + pageSize, list.size()));

        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(200);
        rspData.setMsg("查询成功");
        rspData.setRows(pageRows);
        rspData.setTotal(list.size());
        return rspData;
    }

    @Operation(summary = "查询当前公告发布人已审核通过的入驻申请列表")
    @GetMapping("/audit/approved/list")
    public TableDataInfo approvedList(XfptSupplierSettleApply query) {
        startPage();
        List<XfptSupplierSettleApply> list = supplierSettleApplyService.listApprovedByPublisher(query);
        return getDataTable(list);
    }

    @Operation(summary = "查询当前公告发布人已处理的入驻申请列表")
    @GetMapping("/audit/processed/list")
    public TableDataInfo processedList(XfptSupplierSettleApply query) {
        startPage();
        List<XfptSupplierSettleApply> list = supplierSettleApplyService.listProcessedByPublisher(query);
        return getDataTable(list);
    }

    @Operation(summary = "导出当前公告发布人已审核通过的入驻申请列表")
    @Log(title = "供应商入驻审核", businessType = BusinessType.EXPORT)
    @PostMapping("/audit/approved/export")
    public void approvedExport(HttpServletResponse response, XfptSupplierSettleApply query) {
        List<ApprovedSupplierSettleExportVo> list = supplierSettleApplyService.listApprovedByPublisher(query).stream()
                .map(this::buildApprovedExportVo)
                .collect(Collectors.toList());
        ExcelUtil<ApprovedSupplierSettleExportVo> util = new ExcelUtil<ApprovedSupplierSettleExportVo>(ApprovedSupplierSettleExportVo.class);
        util.exportExcel(response, list, "已通过供应商入驻数据");
    }

    @Operation(summary = "导出当前公告发布人已处理的入驻申请列表")
    @Log(title = "供应商入驻审核", businessType = BusinessType.EXPORT)
    @PostMapping("/audit/processed/export")
    public void processedExport(HttpServletResponse response, XfptSupplierSettleApply query) {
        List<ApprovedSupplierSettleExportVo> list = supplierSettleApplyService.listProcessedByPublisher(query).stream()
                .map(this::buildApprovedExportVo)
                .collect(Collectors.toList());
        ExcelUtil<ApprovedSupplierSettleExportVo> util = new ExcelUtil<ApprovedSupplierSettleExportVo>(ApprovedSupplierSettleExportVo.class);
        util.exportExcel(response, list, "已处理供应商入驻数据");
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

    private ApprovedSupplierSettleExportVo buildApprovedExportVo(XfptSupplierSettleApply item) {
        ApprovedSupplierSettleExportVo vo = new ApprovedSupplierSettleExportVo();
        vo.setSupplierName(item.getSupplierName());
        vo.setSupplierPhone(item.getSupplierPhone());
        vo.setNoticeTitle(item.getNoticeTitle());
        vo.setUsername(item.getUsername());
        vo.setSupplierType(item.getSupplierType());
        vo.setLegalRepresentative(item.getLegalRepresentative());
        vo.setAuditTime(item.getAuditTime());
        vo.setAuditBy(StringUtils.defaultString(item.getAuditBy(), item.getUpdateBy()));
        vo.setAuditStatusName(resolveAuditStatusName(item.getAuditStatus()));
        return vo;
    }

    private String resolveAuditStatusName(String auditStatus) {
        if ("1".equals(auditStatus)) {
            return "已通过";
        }
        if ("2".equals(auditStatus)) {
            return "已驳回";
        }
        return "待审核";
    }
}
