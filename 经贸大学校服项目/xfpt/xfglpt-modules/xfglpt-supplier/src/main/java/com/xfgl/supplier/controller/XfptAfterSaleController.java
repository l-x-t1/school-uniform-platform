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
import com.xfgl.supplier.domain.XfptAfterSale;
import com.xfgl.supplier.service.IXfptAfterSaleService;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;

/**
 * 售后单Controller
 *
 * @author yinet
 * @date 2026-05-01
 */
@Tag(name="售后单管理")
@RestController
@RequestMapping("/afterSale")
public class XfptAfterSaleController extends BaseController {
    @Autowired
    private IXfptAfterSaleService xfptAfterSaleService;

/**
 * 查询售后单列表
 */

@Operation(summary = "查询售后单列表")
@RequiresPermissions("supplier:afterSale:list")
@GetMapping("/list")
    public TableDataInfo list(XfptAfterSale xfptAfterSale) {
        startPage();
        List<XfptAfterSale> list = xfptAfterSaleService.selectXfptAfterSaleList(xfptAfterSale);
        return getDataTable(list);
    }

    /**
     * 导出售后单列表
     */
    @Operation(summary = "导出售后单列表")
    @RequiresPermissions("supplier:afterSale:export")
    @Log(title = "售后单" , businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptAfterSale xfptAfterSale) {
        List<XfptAfterSale> list = xfptAfterSaleService.selectXfptAfterSaleList(xfptAfterSale);
        ExcelUtil<XfptAfterSale> util = new ExcelUtil<XfptAfterSale>(XfptAfterSale. class);
        util.exportExcel(response, list, "售后单数据");
    }

    /**
     * 获取售后单详细信息
     */
    @Operation(summary = "获取售后单详细信息")
    @RequiresPermissions("supplier:afterSale:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
            return success(xfptAfterSaleService.getById(id));
    }

    /**
     * 新增售后单
     */
    @Operation(summary = "新增售后单")
    @RequiresPermissions("supplier:afterSale:add")
    @Log(title = "售后单" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptAfterSale xfptAfterSale) {
            return toAjax(xfptAfterSaleService.save(xfptAfterSale));
    }

    /**
     * 修改售后单
     */
    @Operation(summary = "修改售后单")
    @RequiresPermissions("supplier:afterSale:edit")
    @Log(title = "售后单" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptAfterSale xfptAfterSale) {
            return toAjax(xfptAfterSaleService.updateById(xfptAfterSale));
    }


    /**
     * 删除售后单
     */
    @Operation(summary = "删除售后单")
    @RequiresPermissions("supplier:afterSale:remove")
    @Log(title = "售后单" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
            return toAjax(xfptAfterSaleService.removeByIds(Arrays.asList(ids)));
    }
}
