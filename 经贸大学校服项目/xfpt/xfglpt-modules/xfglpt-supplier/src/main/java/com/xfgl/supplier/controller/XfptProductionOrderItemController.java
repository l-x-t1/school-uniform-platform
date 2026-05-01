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
import com.xfgl.supplier.domain.XfptProductionOrderItem;
import com.xfgl.supplier.service.IXfptProductionOrderItemService;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;

/**
 * 生产单明细Controller
 *
 * @author yinet
 * @date 2026-05-01
 */
@Tag(name="生产单明细管理")
@RestController
@RequestMapping("/productionOrderItem")
public class XfptProductionOrderItemController extends BaseController {
    @Autowired
    private IXfptProductionOrderItemService xfptProductionOrderItemService;

/**
 * 查询生产单明细列表
 */

@Operation(summary = "查询生产单明细列表")
@RequiresPermissions("supplier:productionOrderItem:list")
@GetMapping("/list")
    public TableDataInfo list(XfptProductionOrderItem xfptProductionOrderItem) {
        startPage();
        List<XfptProductionOrderItem> list = xfptProductionOrderItemService.selectXfptProductionOrderItemList(xfptProductionOrderItem);
        return getDataTable(list);
    }

    /**
     * 导出生产单明细列表
     */
    @Operation(summary = "导出生产单明细列表")
    @RequiresPermissions("supplier:productionOrderItem:export")
    @Log(title = "生产单明细" , businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptProductionOrderItem xfptProductionOrderItem) {
        List<XfptProductionOrderItem> list = xfptProductionOrderItemService.selectXfptProductionOrderItemList(xfptProductionOrderItem);
        ExcelUtil<XfptProductionOrderItem> util = new ExcelUtil<XfptProductionOrderItem>(XfptProductionOrderItem. class);
        util.exportExcel(response, list, "生产单明细数据");
    }

    /**
     * 获取生产单明细详细信息
     */
    @Operation(summary = "获取生产单明细详细信息")
    @RequiresPermissions("supplier:productionOrderItem:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
            return success(xfptProductionOrderItemService.getById(id));
    }

    /**
     * 新增生产单明细
     */
    @Operation(summary = "新增生产单明细")
    @RequiresPermissions("supplier:productionOrderItem:add")
    @Log(title = "生产单明细" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptProductionOrderItem xfptProductionOrderItem) {
            return toAjax(xfptProductionOrderItemService.save(xfptProductionOrderItem));
    }

    /**
     * 修改生产单明细
     */
    @Operation(summary = "修改生产单明细")
    @RequiresPermissions("supplier:productionOrderItem:edit")
    @Log(title = "生产单明细" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptProductionOrderItem xfptProductionOrderItem) {
            return toAjax(xfptProductionOrderItemService.updateById(xfptProductionOrderItem));
    }


    /**
     * 删除生产单明细
     */
    @Operation(summary = "删除生产单明细")
    @RequiresPermissions("supplier:productionOrderItem:remove")
    @Log(title = "生产单明细" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
            return toAjax(xfptProductionOrderItemService.removeByIds(Arrays.asList(ids)));
    }
}
