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
 * 企业报名及资格审查Controller
 *
 * @author yinet
 * @date 2026-05-01
 */
@Tag(name="企业报名及资格审查管理")
@RestController
@RequestMapping("/supplier_apply")
public class XfptSupplierApplyController extends BaseController {
    @Autowired
    private IXfptSupplierApplyService xfptSupplierApplyService;

/**
 * 查询企业报名及资格审查列表
 */

@Operation(summary = "查询企业报名及资格审查列表")
@RequiresPermissions("supplier:supplier_apply:list")
@GetMapping("/list")
    public TableDataInfo list(XfptSupplierApply xfptSupplierApply) {
        startPage();
        List<XfptSupplierApply> list = xfptSupplierApplyService.selectXfptSupplierApplyList(xfptSupplierApply);
        return getDataTable(list);
    }

    /**
     * 导出企业报名及资格审查列表
     */
    @Operation(summary = "导出企业报名及资格审查列表")
    @RequiresPermissions("supplier:supplier_apply:export")
    @Log(title = "企业报名及资格审查" , businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptSupplierApply xfptSupplierApply) {
        List<XfptSupplierApply> list = xfptSupplierApplyService.selectXfptSupplierApplyList(xfptSupplierApply);
        ExcelUtil<XfptSupplierApply> util = new ExcelUtil<XfptSupplierApply>(XfptSupplierApply. class);
        util.exportExcel(response, list, "企业报名及资格审查数据");
    }

    /**
     * 获取企业报名及资格审查详细信息
     */
    @Operation(summary = "获取企业报名及资格审查详细信息")
    @RequiresPermissions("supplier:supplier_apply:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
            return success(xfptSupplierApplyService.getById(id));
    }

    /**
     * 新增企业报名及资格审查
     */
    @Operation(summary = "新增企业报名及资格审查")
    @RequiresPermissions("supplier:supplier_apply:add")
    @Log(title = "企业报名及资格审查" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptSupplierApply xfptSupplierApply) {
            return toAjax(xfptSupplierApplyService.save(xfptSupplierApply));
    }

    /**
     * 修改企业报名及资格审查
     */
    @Operation(summary = "修改企业报名及资格审查")
    @RequiresPermissions("supplier:supplier_apply:edit")
    @Log(title = "企业报名及资格审查" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptSupplierApply xfptSupplierApply) {
            return toAjax(xfptSupplierApplyService.updateById(xfptSupplierApply));
    }


    /**
     * 删除企业报名及资格审查
     */
    @Operation(summary = "删除企业报名及资格审查")
    @RequiresPermissions("supplier:supplier_apply:remove")
    @Log(title = "企业报名及资格审查" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
            return toAjax(xfptSupplierApplyService.removeByIds(Arrays.asList(ids)));
    }
}
