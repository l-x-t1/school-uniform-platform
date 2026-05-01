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
import com.xfgl.supplier.domain.XfptOrderInfo;
import com.xfgl.supplier.service.IXfptOrderInfoService;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;

/**
 * 订单主Controller
 *
 * @author yinet
 * @date 2026-05-01
 */
@Tag(name="订单主管理")
@RestController
@RequestMapping("/order_info")
public class XfptOrderInfoController extends BaseController {
    @Autowired
    private IXfptOrderInfoService xfptOrderInfoService;

/**
 * 查询订单主列表
 */

@Operation(summary = "查询订单主列表")
@RequiresPermissions("supplier:order_info:list")
@GetMapping("/list")
    public TableDataInfo list(XfptOrderInfo xfptOrderInfo) {
        startPage();
        List<XfptOrderInfo> list = xfptOrderInfoService.selectXfptOrderInfoList(xfptOrderInfo);
        return getDataTable(list);
    }

    /**
     * 导出订单主列表
     */
    @Operation(summary = "导出订单主列表")
    @RequiresPermissions("supplier:order_info:export")
    @Log(title = "订单主" , businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptOrderInfo xfptOrderInfo) {
        List<XfptOrderInfo> list = xfptOrderInfoService.selectXfptOrderInfoList(xfptOrderInfo);
        ExcelUtil<XfptOrderInfo> util = new ExcelUtil<XfptOrderInfo>(XfptOrderInfo. class);
        util.exportExcel(response, list, "订单主数据");
    }

    /**
     * 获取订单主详细信息
     */
    @Operation(summary = "获取订单主详细信息")
    @RequiresPermissions("supplier:order_info:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
            return success(xfptOrderInfoService.getById(id));
    }

    /**
     * 新增订单主
     */
    @Operation(summary = "新增订单主")
    @RequiresPermissions("supplier:order_info:add")
    @Log(title = "订单主" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptOrderInfo xfptOrderInfo) {
            return toAjax(xfptOrderInfoService.save(xfptOrderInfo));
    }

    /**
     * 修改订单主
     */
    @Operation(summary = "修改订单主")
    @RequiresPermissions("supplier:order_info:edit")
    @Log(title = "订单主" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptOrderInfo xfptOrderInfo) {
            return toAjax(xfptOrderInfoService.updateById(xfptOrderInfo));
    }


    /**
     * 删除订单主
     */
    @Operation(summary = "删除订单主")
    @RequiresPermissions("supplier:order_info:remove")
    @Log(title = "订单主" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
            return toAjax(xfptOrderInfoService.removeByIds(Arrays.asList(ids)));
    }
}
