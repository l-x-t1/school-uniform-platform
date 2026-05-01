package com.xfgl.supplier.controller;

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
import com.xfgl.supplier.domain.XfptDeliveryOrder;
import com.xfgl.supplier.service.IXfptDeliveryOrderService;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;

/**
 * 发货单Controller
 *
 * @author yinet
 * @date 2026-05-01
 */
@Tag(name="发货单管理")
@RestController
@RequestMapping("/deliveryOrder")
public class XfptDeliveryOrderController extends BaseController {
    @Autowired
    private IXfptDeliveryOrderService xfptDeliveryOrderService;

/**
 * 查询发货单列表
 */

@Operation(summary = "查询发货单列表")
@RequiresPermissions("supplier:deliveryOrder:list")
@GetMapping("/list")
    public TableDataInfo list(XfptDeliveryOrder xfptDeliveryOrder) {
        startPage();
        List<XfptDeliveryOrder> list = xfptDeliveryOrderService.selectXfptDeliveryOrderList(xfptDeliveryOrder);
        return getDataTable(list);
    }

    /**
     * 导出发货单列表
     */
    @Operation(summary = "导出发货单列表")
    @RequiresPermissions("supplier:deliveryOrder:export")
    @Log(title = "发货单" , businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptDeliveryOrder xfptDeliveryOrder) {
        List<XfptDeliveryOrder> list = xfptDeliveryOrderService.selectXfptDeliveryOrderList(xfptDeliveryOrder);
        ExcelUtil<XfptDeliveryOrder> util = new ExcelUtil<XfptDeliveryOrder>(XfptDeliveryOrder. class);
        util.exportExcel(response, list, "发货单数据");
    }

    /**
     * 获取发货单详细信息
     */
    @Operation(summary = "获取发货单详细信息")
    @RequiresPermissions("supplier:deliveryOrder:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
            return success(xfptDeliveryOrderService.getById(id));
    }

    /**
     * 新增发货单
     */
    @Operation(summary = "新增发货单")
    @RequiresPermissions("supplier:deliveryOrder:add")
    @Log(title = "发货单" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptDeliveryOrder xfptDeliveryOrder) {
            return toAjax(xfptDeliveryOrderService.save(xfptDeliveryOrder));
    }

    /**
     * 修改发货单
     */
    @Operation(summary = "修改发货单")
    @RequiresPermissions("supplier:deliveryOrder:edit")
    @Log(title = "发货单" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptDeliveryOrder xfptDeliveryOrder) {
            return toAjax(xfptDeliveryOrderService.updateById(xfptDeliveryOrder));
    }


    /**
     * 删除发货单
     */
    @Operation(summary = "删除发货单")
    @RequiresPermissions("supplier:deliveryOrder:remove")
    @Log(title = "发货单" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
            return toAjax(xfptDeliveryOrderService.removeByIds(Arrays.asList(ids)));
    }
}
