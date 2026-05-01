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
import com.xfgl.supplier.domain.XfptSupplierApply;
import com.xfgl.supplier.service.IXfptSupplierApplyService;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;

/**
 * 供应商报名Controller
 *
 * @author yinet
 * @date 2026-05-01
 */
@Tag(name="供应商报名管理")
@RestController
@RequestMapping("/supplierApply")
public class XfptSupplierApplyController extends BaseController {
    @Autowired
    private IXfptSupplierApplyService xfptSupplierApplyService;

/**
 * 查询供应商报名列表
 */

@Operation(summary = "查询供应商报名列表")
@RequiresPermissions("supplier:supplierApply:list")
@GetMapping("/list")
    public TableDataInfo list(XfptSupplierApply xfptSupplierApply) {
        startPage();
        List<XfptSupplierApply> list = xfptSupplierApplyService.selectXfptSupplierApplyList(xfptSupplierApply);
        return getDataTable(list);
    }

    /**
     * 导出供应商报名列表
     */
    @Operation(summary = "导出供应商报名列表")
    @RequiresPermissions("supplier:supplierApply:export")
    @Log(title = "供应商报名" , businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptSupplierApply xfptSupplierApply) {
        List<XfptSupplierApply> list = xfptSupplierApplyService.selectXfptSupplierApplyList(xfptSupplierApply);
        ExcelUtil<XfptSupplierApply> util = new ExcelUtil<XfptSupplierApply>(XfptSupplierApply. class);
        util.exportExcel(response, list, "供应商报名数据");
    }

    /**
     * 获取供应商报名详细信息
     */
    @Operation(summary = "获取供应商报名详细信息")
    @RequiresPermissions("supplier:supplierApply:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
            return success(xfptSupplierApplyService.getById(id));
    }

    /**
     * 新增供应商报名
     */
    @Operation(summary = "新增供应商报名")
    @RequiresPermissions("supplier:supplierApply:add")
    @Log(title = "供应商报名" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptSupplierApply xfptSupplierApply) {
            return toAjax(xfptSupplierApplyService.save(xfptSupplierApply));
    }

    /**
     * 修改供应商报名
     */
    @Operation(summary = "修改供应商报名")
    @RequiresPermissions("supplier:supplierApply:edit")
    @Log(title = "供应商报名" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptSupplierApply xfptSupplierApply) {
            return toAjax(xfptSupplierApplyService.updateById(xfptSupplierApply));
    }


    /**
     * 删除供应商报名
     */
    @Operation(summary = "删除供应商报名")
    @RequiresPermissions("supplier:supplierApply:remove")
    @Log(title = "供应商报名" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
            return toAjax(xfptSupplierApplyService.removeByIds(Arrays.asList(ids)));
    }
}
