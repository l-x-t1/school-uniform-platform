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
import com.xfgl.supplier.domain.XfptSettlementOrder;
import com.xfgl.supplier.service.IXfptSettlementOrderService;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;

/**
 * 结算单Controller
 *
 * @author yinet
 * @date 2026-05-01
 */
@Tag(name="结算单管理")
@RestController
@RequestMapping("/settlementOrder")
public class XfptSettlementOrderController extends BaseController {
    @Autowired
    private IXfptSettlementOrderService xfptSettlementOrderService;

/**
 * 查询结算单列表
 */

@Operation(summary = "查询结算单列表")
@RequiresPermissions("supplier:settlementOrder:list")
@GetMapping("/list")
    public TableDataInfo list(XfptSettlementOrder xfptSettlementOrder) {
        startPage();
        List<XfptSettlementOrder> list = xfptSettlementOrderService.selectXfptSettlementOrderList(xfptSettlementOrder);
        return getDataTable(list);
    }

    /**
     * 导出结算单列表
     */
    @Operation(summary = "导出结算单列表")
    @RequiresPermissions("supplier:settlementOrder:export")
    @Log(title = "结算单" , businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptSettlementOrder xfptSettlementOrder) {
        List<XfptSettlementOrder> list = xfptSettlementOrderService.selectXfptSettlementOrderList(xfptSettlementOrder);
        ExcelUtil<XfptSettlementOrder> util = new ExcelUtil<XfptSettlementOrder>(XfptSettlementOrder. class);
        util.exportExcel(response, list, "结算单数据");
    }

    /**
     * 获取结算单详细信息
     */
    @Operation(summary = "获取结算单详细信息")
    @RequiresPermissions("supplier:settlementOrder:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
            return success(xfptSettlementOrderService.getById(id));
    }

    /**
     * 新增结算单
     */
    @Operation(summary = "新增结算单")
    @RequiresPermissions("supplier:settlementOrder:add")
    @Log(title = "结算单" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptSettlementOrder xfptSettlementOrder) {
            return toAjax(xfptSettlementOrderService.save(xfptSettlementOrder));
    }

    /**
     * 修改结算单
     */
    @Operation(summary = "修改结算单")
    @RequiresPermissions("supplier:settlementOrder:edit")
    @Log(title = "结算单" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptSettlementOrder xfptSettlementOrder) {
            return toAjax(xfptSettlementOrderService.updateById(xfptSettlementOrder));
    }


    /**
     * 删除结算单
     */
    @Operation(summary = "删除结算单")
    @RequiresPermissions("supplier:settlementOrder:remove")
    @Log(title = "结算单" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
            return toAjax(xfptSettlementOrderService.removeByIds(Arrays.asList(ids)));
    }
}
