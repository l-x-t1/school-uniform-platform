package com.xfgl.school.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.xfgl.common.core.constant.ProceConstants;
import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.common.core.utils.StringUtils;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.web.page.TableDataInfo;
import com.xfgl.common.log.annotation.Log;
import com.xfgl.common.log.enums.BusinessType;
import com.xfgl.common.security.annotation.RequiresPermissions;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.domain.*;
import com.xfgl.school.service.*;
import com.xfgl.school.support.SupplierAccessSupport;
import com.xfgl.system.api.domain.SysDept;
import com.xfgl.system.api.domain.SysUser;
import com.xfgl.system.api.model.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 学校与供应商检验报告关联Controller
 *
 * @author yinet
 * @date 2025-04-22
 */
@Tag(name = "学校与供应商检验报告关联管理")
@RestController
@RequestMapping("/suppliceInspectionReport")
public class XfptSuppliceInspectionReportController extends BaseController {
    @Autowired
    private IXfptSuppliceInspectionReportService xfptSuppliceInspectionReportService;
    @Autowired
    private ITeamMeetingService teamMeetingService;
    @Autowired
    private ISupplierMessageService iSupplierMessageService;
    @Autowired
    private SupplierAccessSupport supplierAccessSupport;

    /**
     * 查询学校与供应商检验报告关联列表
     */

    @Operation(summary = "查询学校与供应商检验报告关联列表")
    @RequiresPermissions("school:zjbg:list")
    @GetMapping("/list")
    public TableDataInfo list(XfptSuppliceInspectionReport xfptSuppliceInspectionReport) {
        xfptSuppliceInspectionReport = applyAccessScope(xfptSuppliceInspectionReport);
        startPage();
        List<XfptSuppliceInspectionReport> list = xfptSuppliceInspectionReportService.selectXfptSuppliceInspectionReportList(xfptSuppliceInspectionReport);
        for (XfptSuppliceInspectionReport suppliceInspectionReport : list) {
            SupplierMessage supplierMessage = iSupplierMessageService.getById(suppliceInspectionReport.getSupplierId());
            if (supplierMessage != null) {
                suppliceInspectionReport.setLegalRepresentative(supplierMessage.getLegalRepresentative());
            }
            if (StringUtils.isEmpty(suppliceInspectionReport.getNoticeStatus())) {
                suppliceInspectionReport.setNoticeStatus("0");
            }
            if ("1".equals(suppliceInspectionReport.getNoticeStatus())) {
                suppliceInspectionReport.setNoticeStatusText("已通过");
            } else if ("2".equals(suppliceInspectionReport.getNoticeStatus())) {
                suppliceInspectionReport.setNoticeStatusText("已拒绝");
            } else {
                suppliceInspectionReport.setNoticeStatusText("待审核");
            }
        }
        return getDataTable(list);
    }

    /**
     * 导出学校与供应商检验报告关联列表
     */
    @Operation(summary = "导出学校与供应商检验报告关联列表")
    @RequiresPermissions("school:zjbg:list")
    @Log(title = "学校与供应商检验报告关联", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptSuppliceInspectionReport xfptSuppliceInspectionReport) {
        xfptSuppliceInspectionReport = applyAccessScope(xfptSuppliceInspectionReport);
        List<XfptSuppliceInspectionReport> list = xfptSuppliceInspectionReportService.selectXfptSuppliceInspectionReportList(xfptSuppliceInspectionReport);
        ExcelUtil<XfptSuppliceInspectionReport> util = new ExcelUtil<XfptSuppliceInspectionReport>(XfptSuppliceInspectionReport.class);
        util.exportExcel(response, list, "学校与供应商检验报告关联数据");
    }

    /**
     * 获取学校与供应商检验报告关联详细信息
     */
    @Operation(summary = "获取学校与供应商检验报告关联详细信息")
    @RequiresPermissions("school:zjbg:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        XfptSuppliceInspectionReport byId = xfptSuppliceInspectionReportService.getById(id);
        if (byId == null) {
            return success(null);
        }
        if (byId != null && !canAccessReport(byId)) {
            return error("当前操作没有权限");
        }
        List<XfptAnnoResults> list2 = iXfptAnnoResultsService.lambdaQuery().eq(XfptAnnoResults::getTeamId, byId.getTeamMeetingId()).list();
        if(CollUtil.isNotEmpty(list2)){
            byId.setGgTitle(list2.get(0).getNoticeTitle());
        }
        return success(byId);
    }

    /**
     * 新增学校与供应商检验报告关联
     */
    @Operation(summary = "新增学校与供应商检验报告关联")
//    @RequiresPermissions("system:report:add")
    @Log(title = "学校与供应商检验报告关联", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptSuppliceInspectionReport xfptSuppliceInspectionReport) {
        SysDept dept = SecurityUtils.getLoginUser().getSysUser().getDept();
        String ancestors = dept.getAncestors();

        List<XfptSuppliceInspectionReport> lists = xfptSuppliceInspectionReportService.lambdaQuery()
                .eq(XfptSuppliceInspectionReport::getDeptId, xfptSuppliceInspectionReport.getDeptId())
                .eq(XfptSuppliceInspectionReport::getTeamMeetingId, xfptSuppliceInspectionReport.getTeamMeetingId())
                .list();
        boolean existsSupplier = lists.stream().anyMatch(item ->
                StringUtils.equals(item.getSupplierId(), xfptSuppliceInspectionReport.getSupplierId()));
        if (existsSupplier) {
            throw new ServiceException("该合作厂商已存在，请勿重复添加");
        }
        if (lists.isEmpty()) {
            teamMeetingService.updateTeamMeetingCurrOrder(Long.valueOf(xfptSuppliceInspectionReport.getTeamMeetingId()), ProceConstants.ZJBGGK + "#" + DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        }
        xfptSuppliceInspectionReport.setAncestors(ancestors);
        return toAjax(xfptSuppliceInspectionReportService.save(xfptSuppliceInspectionReport));
    }

    /**
     * 修改学校与供应商检验报告关联
     */
    @Operation(summary = "修改学校与供应商检验报告关联")
//    @RequiresPermissions("system:report:edit")
    @Log(title = "学校与供应商检验报告关联", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptSuppliceInspectionReport xfptSuppliceInspectionReport) {
        return toAjax(xfptSuppliceInspectionReportService.updateById(xfptSuppliceInspectionReport));
    }


    /**
     * 删除学校与供应商检验报告关联
     */
    @Operation(summary = "删除学校与供应商检验报告关联")
//    @RequiresPermissions("system:report:remove")
    @Log(title = "学校与供应商检验报告关联", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(xfptSuppliceInspectionReportService.removeByIds(Arrays.asList(ids)));
    }

    @Autowired
    private IXfptStudentInfoService iXfptStudentInfoService;

    @Autowired
    private IXfptSuppliceInspectionReportSonService iXfptSuppliceInspectionReportSonService;
    @Autowired
    private IXfptAnnoResultsService iXfptAnnoResultsService;

    /**
     * 查询质检报告列表
     * @param simId
     * @return
     */
    @GetMapping("/getNoticeInspect")
    public TableDataInfo getNoticeInspect(String simId) {
        //查询用户绑定的学校
        List<XfptStudentInfo> list1 = iXfptStudentInfoService.lambdaQuery().eq(XfptStudentInfo::getSimId, simId).list();
        if(CollUtil.isEmpty(list1)){
            return getDataTable(null);
        }
        List<String> collect = list1.stream().map(XfptStudentInfo::getShcoolId).collect(Collectors.toList());
        startPage();
        List<XfptSuppliceInspectionReport> list = xfptSuppliceInspectionReportService.lambdaQuery()
                .in(XfptSuppliceInspectionReport::getDeptId, collect).list();
        for (XfptSuppliceInspectionReport xfptSuppliceInspectionReport : list) {
            List<XfptAnnoResults> list2 = iXfptAnnoResultsService.lambdaQuery().eq(XfptAnnoResults::getTeamId, xfptSuppliceInspectionReport.getTeamMeetingId()).list();
            if(CollUtil.isNotEmpty(list2)){
                xfptSuppliceInspectionReport.setGgTitle(list2.get(0).getNoticeTitle());
            }
        }

        return getDataTable(list);
    }

    @GetMapping("/getNoticeInspectFile")
    public AjaxResult getNoticeInspectFile(Long id) {
        List<XfptSuppliceInspectionReportSon> list = iXfptSuppliceInspectionReportSonService.lambdaQuery()
                .eq(XfptSuppliceInspectionReportSon::getReportId, id).orderByDesc(XfptSuppliceInspectionReportSon::getCreateTime).list();
        return AjaxResult.success(list);
    }

    private XfptSuppliceInspectionReport applyAccessScope(XfptSuppliceInspectionReport report) {
        if (report == null) {
            report = new XfptSuppliceInspectionReport();
        }
        SysUser currentUser = getCurrentUser();
        if (supplierAccessSupport.isSupplierUser(currentUser)) {
            Long supplierId = resolveCurrentSupplierId(currentUser);
            if (supplierId != null) {
                report.setSupplierId(String.valueOf(supplierId));
                report.setDeptId(null);
                return report;
            }
        }
        if (currentUser != null && currentUser.getDeptId() != null) {
            report.setDeptId(String.valueOf(currentUser.getDeptId()));
        }
        return report;
    }

    private boolean canAccessReport(XfptSuppliceInspectionReport report) {
        SysUser currentUser = getCurrentUser();
        if (!supplierAccessSupport.isSupplierUser(currentUser)) {
            return true;
        }
        Long supplierId = resolveCurrentSupplierId(currentUser);
        return supplierId == null
                || (report != null && StringUtils.equals(String.valueOf(supplierId), report.getSupplierId()));
    }

    private Long resolveCurrentSupplierId(SysUser currentUser) {
        return supplierAccessSupport.resolveSupplierId(currentUser);
    }

    private SysUser getCurrentUser() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        return loginUser == null ? null : loginUser.getSysUser();
    }
}
