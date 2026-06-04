package com.xfgl.school.controller;
import java.util.List;
import java.util.Date;
import java.util.Arrays;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.xfgl.common.core.utils.StringUtils;
import com.xfgl.common.log.annotation.Log;
import com.xfgl.common.log.enums.BusinessType;
import com.xfgl.common.security.annotation.RequiresPermissions;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.domain.XfptWithdrawApply;
import com.xfgl.school.service.IXfptWithdrawApplyService;
import com.xfgl.school.support.SupplierAccessSupport;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;
import com.xfgl.system.api.domain.SysUser;
import com.xfgl.system.api.model.LoginUser;

/**
 * 供应商提现申请Controller
 *
 * @author yinet
 * @date 2026-05-04
 */
@Tag(name="供应商提现申请管理")
@RestController
@RequestMapping({"/withdrawApply", "/school/withdrawApply", "/xfglpt-school/withdrawApply"})
public class XfptWithdrawApplyController extends BaseController {
    @Autowired
    private IXfptWithdrawApplyService xfptWithdrawApplyService;
    @Autowired
    private SupplierAccessSupport supplierAccessSupport;

/**
 * 查询供应商提现申请列表
 */

@Operation(summary = "查询供应商提现申请列表")
@RequiresPermissions("school:withdrawApply:list")
@GetMapping("/list")
    public TableDataInfo list(XfptWithdrawApply xfptWithdrawApply) {
        xfptWithdrawApply = applySupplierScope(xfptWithdrawApply);
        startPage();
        List<XfptWithdrawApply> list = xfptWithdrawApplyService.selectXfptWithdrawApplyList(xfptWithdrawApply);
        return getDataTable(list);
    }

    /**
     * 导出供应商提现申请列表
     */
    @Operation(summary = "导出供应商提现申请列表")
    @RequiresPermissions("school:withdrawApply:export")
    @Log(title = "供应商提现申请" , businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptWithdrawApply xfptWithdrawApply) {
        xfptWithdrawApply = applySupplierScope(xfptWithdrawApply);
        List<XfptWithdrawApply> list = xfptWithdrawApplyService.selectXfptWithdrawApplyList(xfptWithdrawApply);
        ExcelUtil<XfptWithdrawApply> util = new ExcelUtil<XfptWithdrawApply>(XfptWithdrawApply. class);
        util.exportExcel(response, list, "供应商提现申请数据");
    }

    /**
     * 获取供应商提现申请详细信息
     */
    @Operation(summary = "获取供应商提现申请详细信息")
    @RequiresPermissions("school:withdrawApply:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
            XfptWithdrawApply withdrawApply = xfptWithdrawApplyService.getById(id);
            if (withdrawApply != null && !canAccessSupplierRecord(withdrawApply.getSupplierId())) {
                return error("当前操作没有权限");
            }
            return success(withdrawApply);
    }

    /**
     * 新增供应商提现申请
     */
    @Operation(summary = "新增供应商提现申请")
    @RequiresPermissions("school:withdrawApply:add")
    @Log(title = "供应商提现申请" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptWithdrawApply xfptWithdrawApply) {
            xfptWithdrawApply = applySupplierScope(xfptWithdrawApply);
            fillCurrentSupplierSnapshot(xfptWithdrawApply);
            AjaxResult validateResult = validateWithdrawTimeOrder(xfptWithdrawApply);
            if (validateResult != null) {
                    return validateResult;
            }
            return toAjax(xfptWithdrawApplyService.save(xfptWithdrawApply));
    }

    /**
     * 修改供应商提现申请
     */
    @Operation(summary = "修改供应商提现申请")
    @RequiresPermissions("school:withdrawApply:edit")
    @Log(title = "供应商提现申请" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptWithdrawApply xfptWithdrawApply) {
            AjaxResult validateResult = validateWithdrawTimeOrder(xfptWithdrawApply);
            if (validateResult != null) {
                    return validateResult;
            }
            return toAjax(xfptWithdrawApplyService.updateById(xfptWithdrawApply));
    }

    /**
     * 学校端审核供应商提现申请
     */
    @Operation(summary = "学校端审核供应商提现申请")
    @RequiresPermissions("school:withdrawApply:edit")
    @Log(title = "供应商提现申请审核" , businessType = BusinessType.UPDATE)
    @PutMapping("/audit")
    public AjaxResult audit(@RequestBody XfptWithdrawApply xfptWithdrawApply) {
            if (xfptWithdrawApply == null || xfptWithdrawApply.getId() == null) {
                    return error("提现申请主键不能为空");
            }

            SysUser currentUser = getCurrentUser();
            if (supplierAccessSupport.isSupplierUser(currentUser)) {
                    return error("当前角色不支持审核操作");
            }

            XfptWithdrawApply existing = xfptWithdrawApplyService.getById(xfptWithdrawApply.getId());
            if (existing == null) {
                    return error("提现申请记录不存在");
            }
            if (!"0".equals(existing.getStatus())) {
                    return error("仅待审核记录允许审核");
            }

            String targetStatus = xfptWithdrawApply.getStatus();
            if (!"1".equals(targetStatus) && !"2".equals(targetStatus)) {
                    return error("学校端只允许审核通过或审核拒绝");
            }
            if (StringUtils.isBlank(xfptWithdrawApply.getAuditRemark())) {
                    return error("审核备注不能为空");
            }

            existing.setStatus(targetStatus);
            existing.setAuditRemark(xfptWithdrawApply.getAuditRemark());
            existing.setAuditTime(new Date());
            existing.setAuditUserId(currentUser == null ? null : currentUser.getUserId());
            existing.setAuditUserName(resolveAuditUserName(currentUser));

            AjaxResult validateResult = validateWithdrawTimeOrder(existing);
            if (validateResult != null) {
                    return validateResult;
            }
            return toAjax(xfptWithdrawApplyService.updateById(existing));
    }


    /**
     * 删除供应商提现申请
     */
    @Operation(summary = "删除供应商提现申请")
    @RequiresPermissions("school:withdrawApply:remove")
    @Log(title = "供应商提现申请" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
            return toAjax(xfptWithdrawApplyService.removeByIds(Arrays.asList(ids)));
    }

    private XfptWithdrawApply applySupplierScope(XfptWithdrawApply xfptWithdrawApply) {
            if (xfptWithdrawApply == null) {
                    xfptWithdrawApply = new XfptWithdrawApply();
            }
            Long supplierId = resolveCurrentSupplierId();
            if (supplierId != null) {
                    xfptWithdrawApply.setSupplierId(supplierId);
            }
            return xfptWithdrawApply;
    }

    private void fillCurrentSupplierSnapshot(XfptWithdrawApply xfptWithdrawApply) {
            SysUser currentUser = getCurrentUser();
            if (xfptWithdrawApply == null || !supplierAccessSupport.isSupplierUser(currentUser)
                    || StringUtils.isNotEmpty(xfptWithdrawApply.getSupplierName())) {
                    return;
            }
            String supplierName = StringUtils.isNotEmpty(currentUser.getUserName()) ? currentUser.getUserName() : currentUser.getNickName();
            xfptWithdrawApply.setSupplierName(supplierName);
    }

    private boolean canAccessSupplierRecord(Long supplierId) {
            Long currentSupplierId = resolveCurrentSupplierId();
            return currentSupplierId == null || currentSupplierId.equals(supplierId);
    }

    private Long resolveCurrentSupplierId() {
            SysUser sysUser = getCurrentUser();
            if (!supplierAccessSupport.isSupplierUser(sysUser)) {
                    return null;
            }
            return supplierAccessSupport.resolveSupplierId(sysUser);
    }

    private SysUser getCurrentUser() {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            return loginUser == null ? null : loginUser.getSysUser();
    }

    private String resolveAuditUserName(SysUser currentUser) {
            if (currentUser == null) {
                    return "系统";
            }
            if (StringUtils.isNotEmpty(currentUser.getNickName())) {
                    return currentUser.getNickName();
            }
            if (StringUtils.isNotEmpty(currentUser.getUserName())) {
                    return currentUser.getUserName();
            }
            return "系统";
    }

    private AjaxResult validateWithdrawTimeOrder(XfptWithdrawApply withdrawApply) {
            if (withdrawApply == null) {
                    return error("提现申请数据不能为空");
            }
            if (withdrawApply.getApplyTime() != null && withdrawApply.getAuditTime() != null
                    && !withdrawApply.getApplyTime().before(withdrawApply.getAuditTime())) {
                    return error("申请时间必须小于审核时间");
            }
            if (withdrawApply.getAuditTime() != null && withdrawApply.getPayTime() != null
                    && !withdrawApply.getAuditTime().before(withdrawApply.getPayTime())) {
                    return error("审核时间必须小于打款时间");
            }
            if (withdrawApply.getApplyTime() != null && withdrawApply.getPayTime() != null
                    && !withdrawApply.getApplyTime().before(withdrawApply.getPayTime())) {
                    return error("申请时间必须小于打款时间");
            }
            return null;
    }
}
