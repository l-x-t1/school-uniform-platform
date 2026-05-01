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
import com.xfgl.supplier.domain.XfptSchoolSupplierRel;
import com.xfgl.supplier.service.IXfptSchoolSupplierRelService;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;

/**
 * 学校供应商合作关系Controller
 *
 * @author yinet
 * @date 2026-05-01
 */
@Tag(name="学校供应商合作关系管理")
@RestController
@RequestMapping("/school_supplier_rel")
public class XfptSchoolSupplierRelController extends BaseController {
    @Autowired
    private IXfptSchoolSupplierRelService xfptSchoolSupplierRelService;

/**
 * 查询学校供应商合作关系列表
 */

@Operation(summary = "查询学校供应商合作关系列表")
@RequiresPermissions("supplier:school_supplier_rel:list")
@GetMapping("/list")
    public TableDataInfo list(XfptSchoolSupplierRel xfptSchoolSupplierRel) {
        startPage();
        List<XfptSchoolSupplierRel> list = xfptSchoolSupplierRelService.selectXfptSchoolSupplierRelList(xfptSchoolSupplierRel);
        return getDataTable(list);
    }

    /**
     * 导出学校供应商合作关系列表
     */
    @Operation(summary = "导出学校供应商合作关系列表")
    @RequiresPermissions("supplier:school_supplier_rel:export")
    @Log(title = "学校供应商合作关系" , businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptSchoolSupplierRel xfptSchoolSupplierRel) {
        List<XfptSchoolSupplierRel> list = xfptSchoolSupplierRelService.selectXfptSchoolSupplierRelList(xfptSchoolSupplierRel);
        ExcelUtil<XfptSchoolSupplierRel> util = new ExcelUtil<XfptSchoolSupplierRel>(XfptSchoolSupplierRel. class);
        util.exportExcel(response, list, "学校供应商合作关系数据");
    }

    /**
     * 获取学校供应商合作关系详细信息
     */
    @Operation(summary = "获取学校供应商合作关系详细信息")
    @RequiresPermissions("supplier:school_supplier_rel:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
            return success(xfptSchoolSupplierRelService.getById(id));
    }

    /**
     * 新增学校供应商合作关系
     */
    @Operation(summary = "新增学校供应商合作关系")
    @RequiresPermissions("supplier:school_supplier_rel:add")
    @Log(title = "学校供应商合作关系" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptSchoolSupplierRel xfptSchoolSupplierRel) {
            return toAjax(xfptSchoolSupplierRelService.save(xfptSchoolSupplierRel));
    }

    /**
     * 修改学校供应商合作关系
     */
    @Operation(summary = "修改学校供应商合作关系")
    @RequiresPermissions("supplier:school_supplier_rel:edit")
    @Log(title = "学校供应商合作关系" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptSchoolSupplierRel xfptSchoolSupplierRel) {
            return toAjax(xfptSchoolSupplierRelService.updateById(xfptSchoolSupplierRel));
    }


    /**
     * 删除学校供应商合作关系
     */
    @Operation(summary = "删除学校供应商合作关系")
    @RequiresPermissions("supplier:school_supplier_rel:remove")
    @Log(title = "学校供应商合作关系" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
            return toAjax(xfptSchoolSupplierRelService.removeByIds(Arrays.asList(ids)));
    }
}
