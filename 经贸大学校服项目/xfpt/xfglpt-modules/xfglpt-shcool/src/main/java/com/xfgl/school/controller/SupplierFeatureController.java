package com.xfgl.school.controller;

import com.xfgl.common.core.utils.StringUtils;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.web.page.TableDataInfo;
import com.xfgl.common.security.annotation.RequiresPermissions;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.domain.SupplierMessage;
import com.xfgl.school.domain.XfptSchoolSupplierRel;
import com.xfgl.school.domain.XfptSuppliceInspectionReport;
import com.xfgl.school.domain.XfptSuppliceInspectionReportSon;
import com.xfgl.school.service.ISupplierMessageService;
import com.xfgl.school.service.IXfptSchoolSupplierRelService;
import com.xfgl.school.service.IXfptSuppliceInspectionReportService;
import com.xfgl.school.service.IXfptSuppliceInspectionReportSonService;
import com.xfgl.school.support.SupplierAccessSupport;
import com.xfgl.system.api.domain.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Supplier-side reusable features.
 */
@RestController
@RequestMapping("/supplierFeature")
public class SupplierFeatureController extends BaseController {
    @Autowired
    private ISupplierMessageService supplierMessageService;
    @Autowired
    private IXfptSuppliceInspectionReportService inspectionReportService;
    @Autowired
    private IXfptSuppliceInspectionReportSonService inspectionReportSonService;
    @Autowired
    private IXfptSchoolSupplierRelService schoolSupplierRelService;
    @Autowired
    private SupplierAccessSupport supplierAccessSupport;

    @GetMapping("/qualityReport/list")
    @RequiresPermissions("supplier:qualityReport:list")
    public TableDataInfo qualityReportList(XfptSuppliceInspectionReport query) {
        SysUser user = currentUser();
        if (!user.isAdmin()) {
            SupplierMessage supplier = currentSupplier(user);
            if (supplier == null) {
                startPage();
                return getDataTable(java.util.Collections.emptyList());
            }
            query.setSupplierId(String.valueOf(supplier.getId()));
        }
        startPage();
        List<XfptSuppliceInspectionReport> list = inspectionReportService.selectXfptSuppliceInspectionReportList(query);
        list.forEach(this::fillNoticeStatusText);
        return getDataTable(list);
    }

    @GetMapping("/qualityReport/{id}")
    @RequiresPermissions("supplier:qualityReport:query")
    public AjaxResult qualityReportInfo(@PathVariable Long id) {
        SysUser user = currentUser();
        SupplierMessage supplier = user.isAdmin() ? null : currentSupplier(user);
        XfptSuppliceInspectionReport report = inspectionReportService.getById(id);
        if (report == null || (!user.isAdmin() && (supplier == null || !String.valueOf(supplier.getId()).equals(report.getSupplierId())))) {
            return AjaxResult.error("No permission to view this report.");
        }
        XfptSuppliceInspectionReport query = new XfptSuppliceInspectionReport();
        query.setSupplierId(report.getSupplierId());
        query.setTeamMeetingId(report.getTeamMeetingId());
        query.setDeptId(report.getDeptId());
        List<XfptSuppliceInspectionReport> list = inspectionReportService.selectXfptSuppliceInspectionReportList(query);
        if (!list.isEmpty()) {
            fillNoticeStatusText(list.get(0));
            return success(list.get(0));
        }
        fillNoticeStatusText(report);
        return success(report);
    }

    @GetMapping("/qualityReport/fileList")
    @RequiresPermissions("supplier:qualityReport:query")
    public TableDataInfo qualityReportFileList(XfptSuppliceInspectionReportSon query) {
        SysUser user = currentUser();
        SupplierMessage supplier = user.isAdmin() ? null : currentSupplier(user);
        if (!user.isAdmin() && (supplier == null || !reportBelongsToSupplier(query.getReportId(), supplier))) {
            startPage();
            return getDataTable(java.util.Collections.emptyList());
        }
        startPage();
        List<XfptSuppliceInspectionReportSon> list = inspectionReportSonService.selectXfptSuppliceInspectionReportSonList(query);
        return getDataTable(list);
    }

    @PostMapping("/qualityReport")
    @RequiresPermissions("supplier:qualityReport:add")
    public AjaxResult addQualityReport(@RequestBody XfptSuppliceInspectionReportSon reportSon) {
        SysUser user = currentUser();
        SupplierMessage supplier = user.isAdmin() ? null : currentSupplier(user);
        if (!user.isAdmin() && (supplier == null || !reportBelongsToSupplier(reportSon.getReportId(), supplier))) {
            return AjaxResult.error("No permission to add this report.");
        }
        Long count = inspectionReportSonService.lambdaQuery()
                .eq(XfptSuppliceInspectionReportSon::getReportId, reportSon.getReportId())
                .count();
        reportSon.setType(String.valueOf(count + 1));
        return toAjax(inspectionReportSonService.save(reportSon));
    }

    @GetMapping("/serviceSchool/list")
    @RequiresPermissions("supplier:serviceSchool:list")
    public TableDataInfo serviceSchoolList(XfptSchoolSupplierRel query, String type) {
        SysUser user = currentUser();
        if (!user.isAdmin()) {
            SupplierMessage supplier = currentSupplier(user);
            if (supplier == null) {
                startPage();
                return getDataTable(java.util.Collections.emptyList());
            }
            query.setSupplierId(supplier.getId());
        }
        if ("history".equals(type)) {
            query.setCooperateStatus("3");
        } else if ("notice".equals(type)) {
            query.setCooperateStatus("4");
        } else {
            query.setCooperateStatus("1");
        }
        startPage();
        List<XfptSchoolSupplierRel> list = schoolSupplierRelService.selectXfptSchoolSupplierRelList(query);
        return getDataTable(list);
    }

    private boolean reportBelongsToSupplier(String reportId, SupplierMessage supplier) {
        if (StringUtils.isEmpty(reportId)) {
            return false;
        }
        XfptSuppliceInspectionReport report = inspectionReportService.getById(reportId);
        return report != null && String.valueOf(supplier.getId()).equals(report.getSupplierId());
    }

    private SysUser currentUser() {
        return SecurityUtils.getLoginUser().getSysUser();
    }

    private SupplierMessage currentSupplier(SysUser user) {
        Long supplierId = supplierAccessSupport.resolveSupplierId(user);
        return supplierId == null ? null : supplierMessageService.getById(supplierId);
    }

    private void fillNoticeStatusText(XfptSuppliceInspectionReport report) {
        if (StringUtils.isEmpty(report.getNoticeStatus())) {
            report.setNoticeStatus("0");
        }
        if ("1".equals(report.getNoticeStatus())) {
            report.setNoticeStatusText("\u5df2\u901a\u8fc7");
        } else if ("2".equals(report.getNoticeStatus())) {
            report.setNoticeStatusText("\u5df2\u62d2\u7edd");
        } else {
            report.setNoticeStatusText("\u5f85\u5ba1\u6838");
        }
    }
}
