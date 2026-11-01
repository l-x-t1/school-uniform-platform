package com.xfgl.school.controller;

import com.xfgl.common.core.utils.poi.ExcelUtil;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.web.page.TableDataInfo;
import com.xfgl.common.log.annotation.Log;
import com.xfgl.common.log.enums.BusinessType;
import com.xfgl.common.security.annotation.RequiresPermissions;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.domain.XfptDeliveryOrder;
import com.xfgl.school.service.IXfptDeliveryOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 发货单Controller
 */
@Tag(name = "发货单管理")
@RestController
@RequestMapping("/deliveryOrder")
public class XfptDeliveryOrderController extends BaseController {
    @Autowired
    private IXfptDeliveryOrderService xfptDeliveryOrderService;

    @Operation(summary = "查询发货单列表")
    @RequiresPermissions("school:deliveryOrder:list")
    @GetMapping("/list")
    public TableDataInfo list(XfptDeliveryOrder xfptDeliveryOrder) {
        startPage();
        List<XfptDeliveryOrder> list = xfptDeliveryOrderService.selectXfptDeliveryOrderList(xfptDeliveryOrder);
        return getDataTable(list);
    }

    @Operation(summary = "查询发货单统计列表")
    @RequiresPermissions("school:tgfh:list")
    @GetMapping("/statistics/list")
    public TableDataInfo statisticsList(XfptDeliveryOrder xfptDeliveryOrder) {
        xfptDeliveryOrder.setDeptId(SecurityUtils.getLoginUser().getSysUser().getDeptId());
        startPage();
        List<XfptDeliveryOrder> list = xfptDeliveryOrderService.selectXfptDeliveryOrderList(xfptDeliveryOrder);
        return getDataTable(list);
    }

    @Operation(summary = "查询发货单统计详情")
    @RequiresPermissions("school:tgfh:list")
    @GetMapping("/statistics/{id}")
    public AjaxResult statisticsInfo(@PathVariable("id") Long id) {
        XfptDeliveryOrder deliveryOrder = xfptDeliveryOrderService.selectXfptDeliveryOrderById(id);
        Long deptId = SecurityUtils.getLoginUser().getSysUser().getDeptId();
        if (deliveryOrder == null || deliveryOrder.getDeptId() == null || !deliveryOrder.getDeptId().equals(deptId)) {
            return success(null);
        }
        return success(deliveryOrder);
    }

    @Operation(summary = "导出发货单列表")
    @RequiresPermissions("school:deliveryOrder:export")
    @Log(title = "发货单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptDeliveryOrder xfptDeliveryOrder) {
        List<XfptDeliveryOrder> list = xfptDeliveryOrderService.selectXfptDeliveryOrderList(xfptDeliveryOrder);
        ExcelUtil<XfptDeliveryOrder> util = new ExcelUtil<XfptDeliveryOrder>(XfptDeliveryOrder.class);
        util.exportExcel(response, list, "发货单数据");
    }

    @Operation(summary = "获取发货单详细信息")
    @RequiresPermissions("school:deliveryOrder:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(xfptDeliveryOrderService.selectXfptDeliveryOrderById(id));
    }

    @Operation(summary = "新增发货单")
    @RequiresPermissions("school:deliveryOrder:add")
    @Log(title = "发货单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptDeliveryOrder xfptDeliveryOrder) {
        return toAjax(xfptDeliveryOrderService.insertXfptDeliveryOrder(xfptDeliveryOrder));
    }

    @Operation(summary = "修改发货单")
    @RequiresPermissions("school:deliveryOrder:edit")
    @Log(title = "发货单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptDeliveryOrder xfptDeliveryOrder) {
        return toAjax(xfptDeliveryOrderService.updateXfptDeliveryOrder(xfptDeliveryOrder));
    }

    @Operation(summary = "删除发货单")
    @RequiresPermissions("school:deliveryOrder:remove")
    @Log(title = "发货单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(xfptDeliveryOrderService.deleteXfptDeliveryOrderByIds(ids));
    }
}
