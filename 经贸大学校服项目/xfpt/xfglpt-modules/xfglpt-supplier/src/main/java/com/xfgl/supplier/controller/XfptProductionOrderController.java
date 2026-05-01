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
import com.xfgl.supplier.domain.XfptProductionOrder;
import com.xfgl.supplier.service.IXfptProductionOrderService;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;

/**
 * 生产单主Controller
 *
 * @author yinet
 * @date 2026-05-01
 */
@Tag(name="生产单主管理")
@RestController
@RequestMapping("/production_order")
public class XfptProductionOrderController extends BaseController {
    @Autowired
    private IXfptProductionOrderService xfptProductionOrderService;

/**
 * 查询生产单主列表
 */

@Operation(summary = "查询生产单主列表")
@RequiresPermissions("supplier:production_order:list")
@GetMapping("/list")
    public TableDataInfo list(XfptProductionOrder xfptProductionOrder) {
        startPage();
        List<XfptProductionOrder> list = xfptProductionOrderService.selectXfptProductionOrderList(xfptProductionOrder);
        return getDataTable(list);
    }

    /**
     * 导出生产单主列表
     */
    @Operation(summary = "导出生产单主列表")
    @RequiresPermissions("supplier:production_order:export")
    @Log(title = "生产单主" , businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptProductionOrder xfptProductionOrder) {
        List<XfptProductionOrder> list = xfptProductionOrderService.selectXfptProductionOrderList(xfptProductionOrder);
        ExcelUtil<XfptProductionOrder> util = new ExcelUtil<XfptProductionOrder>(XfptProductionOrder. class);
        util.exportExcel(response, list, "生产单主数据");
    }

    /**
     * 获取生产单主详细信息
     */
    @Operation(summary = "获取生产单主详细信息")
    @RequiresPermissions("supplier:production_order:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
            return success(xfptProductionOrderService.getById(id));
    }

    /**
     * 新增生产单主
     */
    @Operation(summary = "新增生产单主")
    @RequiresPermissions("supplier:production_order:add")
    @Log(title = "生产单主" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptProductionOrder xfptProductionOrder) {
            return toAjax(xfptProductionOrderService.save(xfptProductionOrder));
    }

    /**
     * 修改生产单主
     */
    @Operation(summary = "修改生产单主")
    @RequiresPermissions("supplier:production_order:edit")
    @Log(title = "生产单主" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptProductionOrder xfptProductionOrder) {
            return toAjax(xfptProductionOrderService.updateById(xfptProductionOrder));
    }


    /**
     * 删除生产单主
     */
    @Operation(summary = "删除生产单主")
    @RequiresPermissions("supplier:production_order:remove")
    @Log(title = "生产单主" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
            return toAjax(xfptProductionOrderService.removeByIds(Arrays.asList(ids)));
    }
}
