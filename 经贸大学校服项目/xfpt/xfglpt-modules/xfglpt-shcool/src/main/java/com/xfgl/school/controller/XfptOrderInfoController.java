package com.xfgl.school.controller;
import java.util.List;
import java.util.Arrays;
import java.io.IOException;
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
import com.xfgl.common.log.annotation.Log;
import com.xfgl.common.log.enums.BusinessType;
import com.xfgl.common.security.annotation.RequiresPermissions;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.domain.XfptOrderInfo;
import com.xfgl.school.service.IXfptOrderInfoService;
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
 * 订单信息Controller
 *
 * @author yinet
 * @date 2026-05-04
 */
@Tag(name="订单信息管理")
@RestController
@RequestMapping({"/orderInfo", "/school/orderInfo", "/xfglpt-school/orderInfo"})
public class XfptOrderInfoController extends BaseController {
    private static final Long NO_ACCESS_SUPPLIER_ID = -1L;

    @Autowired
    private IXfptOrderInfoService xfptOrderInfoService;

    @Autowired
    private SupplierAccessSupport supplierAccessSupport;

/**
 * 查询订单信息列表
 */

@Operation(summary = "查询订单信息列表")
@RequiresPermissions("school:orderInfo:list")
@GetMapping("/list")
    public TableDataInfo list(XfptOrderInfo xfptOrderInfo) {
        xfptOrderInfo = applySupplierScope(xfptOrderInfo);
        startPage();
        List<XfptOrderInfo> list = xfptOrderInfoService.selectXfptOrderInfoList(xfptOrderInfo);
        return getDataTable(list);
    }

    /**
     * 导出订单信息列表
     */
    @Operation(summary = "导出订单信息列表")
    @RequiresPermissions("school:orderInfo:export")
    @Log(title = "订单信息" , businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptOrderInfo xfptOrderInfo) {
        xfptOrderInfo = applySupplierScope(xfptOrderInfo);
        List<XfptOrderInfo> list = xfptOrderInfoService.selectXfptOrderInfoList(xfptOrderInfo);
        ExcelUtil<XfptOrderInfo> util = new ExcelUtil<XfptOrderInfo>(XfptOrderInfo. class);
        util.exportExcel(response, list, "订单信息数据");
    }

    /**
     * 获取订单信息详细信息
     */
    @Operation(summary = "获取订单信息详细信息")
    @RequiresPermissions("school:orderInfo:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
            XfptOrderInfo orderInfo = xfptOrderInfoService.getById(id);
            if (orderInfo == null) {
                return error("订单不存在");
            }
            if (!canAccessSupplierRecord(orderInfo.getSupplierId())) {
                return error("当前操作没有权限");
            }
            return success(xfptOrderInfoService.selectXfptOrderInfoById(id));
    }

    /**
     * 新增订单信息
     */
    @Operation(summary = "新增订单信息")
    @RequiresPermissions("school:orderInfo:add")
    @Log(title = "订单信息" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptOrderInfo xfptOrderInfo) {
            if (isSupplierUserWithoutBinding()) {
                return error("当前供应商账号未关联厂商，暂无权限操作订单");
            }
            xfptOrderInfo = applySupplierOwnerForWrite(xfptOrderInfo, null);
            int rows = xfptOrderInfoService.insertXfptOrderInfo(xfptOrderInfo);
            if (rows <= 0) {
                return error("新增订单失败");
            }
            return success(xfptOrderInfo);
    }

    /**
     * 修改订单信息
     */
    @Operation(summary = "修改订单信息")
    @RequiresPermissions("school:orderInfo:edit")
    @Log(title = "订单信息" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptOrderInfo xfptOrderInfo) {
            if (xfptOrderInfo == null || xfptOrderInfo.getId() == null) {
                return error("订单ID不能为空");
            }
            XfptOrderInfo currentOrder = xfptOrderInfoService.getById(xfptOrderInfo.getId());
            if (currentOrder == null) {
                return error("订单不存在");
            }
            if (!canAccessSupplierRecord(currentOrder.getSupplierId())) {
                return error("当前操作没有权限");
            }
            if (isSupplierUserWithoutBinding()) {
                return error("当前供应商账号未关联厂商，暂无权限操作订单");
            }
            xfptOrderInfo = applySupplierOwnerForWrite(xfptOrderInfo, currentOrder);
            return toAjax(xfptOrderInfoService.updateXfptOrderInfo(xfptOrderInfo));
    }


    /**
     * 删除订单信息
     */
    @Operation(summary = "删除订单信息")
    @RequiresPermissions("school:orderInfo:remove")
    @Log(title = "订单信息" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
            if (isSupplierUserWithoutBinding()) {
                return error("当前供应商账号未关联厂商，暂无权限操作订单");
            }
            for (Long id : ids) {
                XfptOrderInfo orderInfo = xfptOrderInfoService.getById(id);
                if (orderInfo == null) {
                    return error("订单不存在或已删除");
                }
                if (!canAccessSupplierRecord(orderInfo.getSupplierId())) {
                    return error("存在无权限操作的订单");
                }
            }
            return toAjax(xfptOrderInfoService.deleteXfptOrderInfoByIds(ids));
    }

    private XfptOrderInfo applySupplierScope(XfptOrderInfo xfptOrderInfo) {
        if (xfptOrderInfo == null) {
            xfptOrderInfo = new XfptOrderInfo();
        }
        SupplierScope scope = getCurrentSupplierScope();
        if (!scope.isSupplierUser()) {
            return xfptOrderInfo;
        }
        xfptOrderInfo.setSupplierId(scope.getSupplierId() == null ? NO_ACCESS_SUPPLIER_ID : scope.getSupplierId());
        return xfptOrderInfo;
    }

    private XfptOrderInfo applySupplierOwnerForWrite(XfptOrderInfo xfptOrderInfo, XfptOrderInfo currentOrder) {
        SupplierScope scope = getCurrentSupplierScope();
        if (!scope.isSupplierUser() || xfptOrderInfo == null) {
            return xfptOrderInfo;
        }
        Long supplierId = currentOrder == null ? scope.getSupplierId() : currentOrder.getSupplierId();
        xfptOrderInfo.setSupplierId(supplierId);
        if (currentOrder != null) {
            xfptOrderInfo.setSupplierName(currentOrder.getSupplierName());
        }
        return xfptOrderInfo;
    }

    private boolean canAccessSupplierRecord(Long supplierId) {
        SupplierScope scope = getCurrentSupplierScope();
        if (!scope.isSupplierUser()) {
            return true;
        }
        return scope.getSupplierId() != null && scope.getSupplierId().equals(supplierId);
    }

    private boolean isSupplierUserWithoutBinding() {
        SupplierScope scope = getCurrentSupplierScope();
        return scope.isSupplierUser() && scope.getSupplierId() == null;
    }

    private SupplierScope getCurrentSupplierScope() {
        SysUser sysUser = getCurrentUser();
        if (!supplierAccessSupport.isSupplierUser(sysUser)) {
            return SupplierScope.nonSupplier();
        }
        return SupplierScope.supplier(supplierAccessSupport.resolveSupplierId(sysUser));
    }

    private SysUser getCurrentUser() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        return loginUser == null ? null : loginUser.getSysUser();
    }

    private static class SupplierScope {
        private final boolean supplierUser;
        private final Long supplierId;

        private SupplierScope(boolean supplierUser, Long supplierId) {
            this.supplierUser = supplierUser;
            this.supplierId = supplierId;
        }

        static SupplierScope nonSupplier() {
            return new SupplierScope(false, null);
        }

        static SupplierScope supplier(Long supplierId) {
            return new SupplierScope(true, supplierId);
        }

        boolean isSupplierUser() {
            return supplierUser;
        }

        Long getSupplierId() {
            return supplierId;
        }
    }
}
