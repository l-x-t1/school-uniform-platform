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
import com.xfgl.supplier.domain.XfptSettlementOrderItem;
import com.xfgl.supplier.service.IXfptSettlementOrderItemService;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;

/**
 * 结算明细Controller
 *
 * @author yinet
 * @date 2026-05-01
 */
@Tag(name="结算明细管理")
@RestController
@RequestMapping("/settlementOrderItem")
public class XfptSettlementOrderItemController extends BaseController {
    @Autowired
    private IXfptSettlementOrderItemService xfptSettlementOrderItemService;

/**
 * 查询结算明细列表
 */

@Operation(summary = "查询结算明细列表")
@RequiresPermissions("supplier:settlementOrderItem:list")
@GetMapping("/list")
    public TableDataInfo list(XfptSettlementOrderItem xfptSettlementOrderItem) {
        startPage();
        List<XfptSettlementOrderItem> list = xfptSettlementOrderItemService.selectXfptSettlementOrderItemList(xfptSettlementOrderItem);
        return getDataTable(list);
    }

    /**
     * 导出结算明细列表
     */
    @Operation(summary = "导出结算明细列表")
    @RequiresPermissions("supplier:settlementOrderItem:export")
    @Log(title = "结算明细" , businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptSettlementOrderItem xfptSettlementOrderItem) {
        List<XfptSettlementOrderItem> list = xfptSettlementOrderItemService.selectXfptSettlementOrderItemList(xfptSettlementOrderItem);
        ExcelUtil<XfptSettlementOrderItem> util = new ExcelUtil<XfptSettlementOrderItem>(XfptSettlementOrderItem. class);
        util.exportExcel(response, list, "结算明细数据");
    }

    /**
     * 获取结算明细详细信息
     */
    @Operation(summary = "获取结算明细详细信息")
    @RequiresPermissions("supplier:settlementOrderItem:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
            return success(xfptSettlementOrderItemService.getById(id));
    }

    /**
     * 新增结算明细
     */
    @Operation(summary = "新增结算明细")
    @RequiresPermissions("supplier:settlementOrderItem:add")
    @Log(title = "结算明细" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptSettlementOrderItem xfptSettlementOrderItem) {
            return toAjax(xfptSettlementOrderItemService.save(xfptSettlementOrderItem));
    }

    /**
     * 修改结算明细
     */
    @Operation(summary = "修改结算明细")
    @RequiresPermissions("supplier:settlementOrderItem:edit")
    @Log(title = "结算明细" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptSettlementOrderItem xfptSettlementOrderItem) {
            return toAjax(xfptSettlementOrderItemService.updateById(xfptSettlementOrderItem));
    }


    /**
     * 删除结算明细
     */
    @Operation(summary = "删除结算明细")
    @RequiresPermissions("supplier:settlementOrderItem:remove")
    @Log(title = "结算明细" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
            return toAjax(xfptSettlementOrderItemService.removeByIds(Arrays.asList(ids)));
    }
}
