package com.xfgl.school.controller;
import java.util.List;
import java.util.Arrays;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.xfgl.common.log.annotation.Log;
import com.xfgl.common.log.enums.BusinessType;
import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.system.api.domain.SysDept;
import com.xfgl.common.security.annotation.RequiresPermissions;
import com.xfgl.school.domain.XfptOrganizationMode;
import com.xfgl.school.domain.XfptSupplierApply;
import com.xfgl.school.domain.XfptSupplierApplyLog;
import com.xfgl.school.domain.vo.SupplierApplyAuditRequest;
import com.xfgl.school.service.IXfptOrganizationModeService;
import com.xfgl.school.service.IXfptSupplierApplyService;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;

/**
 * 供应商报名Controller
 *
 * @author yinet
 * @date 2026-05-04
 */
@Tag(name="供应商报名管理")
@RestController
@RequestMapping("/supplierApply")
public class XfptSupplierApplyController extends BaseController {
    @Autowired
    private IXfptSupplierApplyService xfptSupplierApplyService;
    @Autowired
    private IXfptOrganizationModeService xfptOrganizationModeService;

/**
 * 查询供应商报名列表
 */

@Operation(summary = "查询供应商报名列表")
@RequiresPermissions("school:supplierApply:list")
@GetMapping("/list")
    public TableDataInfo list(XfptSupplierApply xfptSupplierApply) {
        if (!SecurityUtils.isAdmin(SecurityUtils.getUserId())) {
            SysDept dept = SecurityUtils.getLoginUser().getSysUser().getDept();
            if (dept != null) {
                xfptSupplierApply.setDeptId(dept.getDeptId());
            }
        }
        startPage();
        List<XfptSupplierApply> list = xfptSupplierApplyService.selectXfptSupplierApplyList(xfptSupplierApply);
        return getDataTable(list);
    }

    /**
     * 导出供应商报名列表
     */
    @Operation(summary = "导出供应商报名列表")
    @RequiresPermissions("school:supplierApply:export")
    @Log(title = "供应商报名" , businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptSupplierApply xfptSupplierApply) {
        List<XfptSupplierApply> list = xfptSupplierApplyService.selectXfptSupplierApplyList(xfptSupplierApply);
        ExcelUtil<XfptSupplierApply> util = new ExcelUtil<XfptSupplierApply>(XfptSupplierApply. class);
        util.exportExcel(response, list, "供应商报名数据");
    }

    /**
     * 获取供应商报名详细信息
     */
    @Operation(summary = "获取供应商报名详细信息")
    @RequiresPermissions("school:supplierApply:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
            return success(xfptSupplierApplyService.selectXfptSupplierApplyById(id));
    }

    @Operation(summary = "获取供应商报名日志")
    @RequiresPermissions("school:supplierApply:query")
    @GetMapping("/log/{applyId}")
    public AjaxResult getLogs(@PathVariable("applyId") Long applyId) {
        List<XfptSupplierApplyLog> logs = xfptSupplierApplyService.selectApplyLogs(applyId);
        return success(logs);
    }

    @Operation(summary = "审核通过供应商报名")
    @RequiresPermissions("school:supplierApply:edit")
    @Log(title = "供应商报名审核", businessType = BusinessType.UPDATE)
    @PostMapping("/audit/pass")
    public AjaxResult auditPass(@RequestBody SupplierApplyAuditRequest request) {
        Long deptId = SecurityUtils.isAdmin(SecurityUtils.getUserId())
                ? null
                : SecurityUtils.getLoginUser().getSysUser().getDeptId();
        xfptSupplierApplyService.auditPass(deptId, request);
        return success();
    }

    @Operation(summary = "审核驳回供应商报名")
    @RequiresPermissions("school:supplierApply:edit")
    @Log(title = "供应商报名审核", businessType = BusinessType.UPDATE)
    @PostMapping("/audit/reject")
    public AjaxResult auditReject(@RequestBody SupplierApplyAuditRequest request) {
        if (request == null || StrUtil.isBlank(request.getAuditRemark())) {
            throw new ServiceException("驳回时必须填写驳回原因");
        }
        Long deptId = SecurityUtils.isAdmin(SecurityUtils.getUserId())
                ? null
                : SecurityUtils.getLoginUser().getSysUser().getDeptId();
        xfptSupplierApplyService.auditReject(deptId, request);
        return success();
    }

    /**
     * 新增供应商报名
     */
    @Operation(summary = "新增供应商报名")
    @RequiresPermissions("school:supplierApply:add")
    @Log(title = "供应商报名" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptSupplierApply xfptSupplierApply) {
            XfptOrganizationMode organizationMode = xfptOrganizationModeService.getById(xfptSupplierApply.getNoticeId());
            if (organizationMode == null) {
                throw new ServiceException("采购公告不存在");
            }
            if (organizationMode.getSignupLimit() != null && organizationMode.getSignupLimit() > 0) {
                long currentCount = xfptSupplierApplyService.lambdaQuery()
                        .eq(XfptSupplierApply::getNoticeId, xfptSupplierApply.getNoticeId())
                        .ne(XfptSupplierApply::getApplyStatus, "2")
                        .count();
                if (currentCount >= organizationMode.getSignupLimit()) {
                    throw new ServiceException("当前采购公告报名人数已满");
                }
            }
            SysDept dept = SecurityUtils.getLoginUser().getSysUser().getDept();
            if (xfptSupplierApply.getDeptId() == null) {
                xfptSupplierApply.setDeptId(dept.getDeptId());
            }
            if (xfptSupplierApply.getSchoolId() == null) {
                xfptSupplierApply.setSchoolId(dept.getDeptId());
            }
            if (StrUtil.isBlank(xfptSupplierApply.getApplyStatus())) {
                xfptSupplierApply.setApplyStatus("1");
            }
            return toAjax(xfptSupplierApplyService.save(xfptSupplierApply));
    }

    /**
     * 修改供应商报名
     */
    @Operation(summary = "修改供应商报名")
    @RequiresPermissions("school:supplierApply:edit")
    @Log(title = "供应商报名" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptSupplierApply xfptSupplierApply) {
            return toAjax(xfptSupplierApplyService.updateById(xfptSupplierApply));
    }


    /**
     * 删除供应商报名
     */
    @Operation(summary = "删除供应商报名")
    @RequiresPermissions("school:supplierApply:remove")
    @Log(title = "供应商报名" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
            return toAjax(xfptSupplierApplyService.removeByIds(Arrays.asList(ids)));
    }
}
