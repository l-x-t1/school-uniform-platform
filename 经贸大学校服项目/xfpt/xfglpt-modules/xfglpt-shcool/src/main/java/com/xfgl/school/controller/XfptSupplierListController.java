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
import com.xfgl.school.domain.XfptSupplierList;
import com.xfgl.school.service.IXfptSupplierListService;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;

/**
 * 确定供应商名单Controller
 *
 * @author yinet
 * @date 2025-05-23
 */
@Tag(name="确定供应商名单管理")
@RestController
@RequestMapping("/suplist")
public class XfptSupplierListController extends BaseController {
    @Autowired
    private IXfptSupplierListService xfptSupplierListService;

/**
 * 查询确定供应商名单列表
 */

@Operation(summary = "查询确定供应商名单列表")
@RequiresPermissions("school:suplist:list")
@GetMapping("/list")
    public TableDataInfo list(XfptSupplierList xfptSupplierList) {
        startPage();
        List<XfptSupplierList> list = xfptSupplierListService.selectXfptSupplierListList(xfptSupplierList);
        return getDataTable(list);
    }

    /**
     * 导出确定供应商名单列表
     */
    @Operation(summary = "导出确定供应商名单列表")
    @RequiresPermissions("school:suplist:export")
    @Log(title = "确定供应商名单" , businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptSupplierList xfptSupplierList) {
        List<XfptSupplierList> list = xfptSupplierListService.selectXfptSupplierListList(xfptSupplierList);
        ExcelUtil<XfptSupplierList> util = new ExcelUtil<XfptSupplierList>(XfptSupplierList. class);
        util.exportExcel(response, list, "确定供应商名单数据");
    }

    /**
     * 获取确定供应商名单详细信息
     */
    @Operation(summary = "获取确定供应商名单详细信息")
    @RequiresPermissions("school:suplist:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
            return success(xfptSupplierListService.getById(id));
    }

    /**
     * 新增确定供应商名单
     */
    @Operation(summary = "新增确定供应商名单")
    @RequiresPermissions("school:suplist:add")
    @Log(title = "确定供应商名单" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptSupplierList xfptSupplierList) {
            return toAjax(xfptSupplierListService.save(xfptSupplierList));
    }

    /**
     * 修改确定供应商名单
     */
    @Operation(summary = "修改确定供应商名单")
    @RequiresPermissions("school:suplist:edit")
    @Log(title = "确定供应商名单" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptSupplierList xfptSupplierList) {
            return toAjax(xfptSupplierListService.updateById(xfptSupplierList));
    }


    /**
     * 删除确定供应商名单
     */
    @Operation(summary = "删除确定供应商名单")
    @RequiresPermissions("school:suplist:remove")
    @Log(title = "确定供应商名单" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
            return toAjax(xfptSupplierListService.removeByIds(Arrays.asList(ids)));
    }
}
