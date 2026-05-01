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
import com.xfgl.supplier.domain.XfptSchoolSupplierGoodsRel;
import com.xfgl.supplier.service.IXfptSchoolSupplierGoodsRelService;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;

/**
 * 学校供应商合作商品Controller
 *
 * @author yinet
 * @date 2026-05-01
 */
@Tag(name="学校供应商合作商品管理")
@RestController
@RequestMapping("/schoolSupplierGoodsRel")
public class XfptSchoolSupplierGoodsRelController extends BaseController {
    @Autowired
    private IXfptSchoolSupplierGoodsRelService xfptSchoolSupplierGoodsRelService;

/**
 * 查询学校供应商合作商品列表
 */

@Operation(summary = "查询学校供应商合作商品列表")
@RequiresPermissions("supplier:schoolSupplierGoodsRel:list")
@GetMapping("/list")
    public TableDataInfo list(XfptSchoolSupplierGoodsRel xfptSchoolSupplierGoodsRel) {
        startPage();
        List<XfptSchoolSupplierGoodsRel> list = xfptSchoolSupplierGoodsRelService.selectXfptSchoolSupplierGoodsRelList(xfptSchoolSupplierGoodsRel);
        return getDataTable(list);
    }

    /**
     * 导出学校供应商合作商品列表
     */
    @Operation(summary = "导出学校供应商合作商品列表")
    @RequiresPermissions("supplier:schoolSupplierGoodsRel:export")
    @Log(title = "学校供应商合作商品" , businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptSchoolSupplierGoodsRel xfptSchoolSupplierGoodsRel) {
        List<XfptSchoolSupplierGoodsRel> list = xfptSchoolSupplierGoodsRelService.selectXfptSchoolSupplierGoodsRelList(xfptSchoolSupplierGoodsRel);
        ExcelUtil<XfptSchoolSupplierGoodsRel> util = new ExcelUtil<XfptSchoolSupplierGoodsRel>(XfptSchoolSupplierGoodsRel. class);
        util.exportExcel(response, list, "学校供应商合作商品数据");
    }

    /**
     * 获取学校供应商合作商品详细信息
     */
    @Operation(summary = "获取学校供应商合作商品详细信息")
    @RequiresPermissions("supplier:schoolSupplierGoodsRel:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
            return success(xfptSchoolSupplierGoodsRelService.getById(id));
    }

    /**
     * 新增学校供应商合作商品
     */
    @Operation(summary = "新增学校供应商合作商品")
    @RequiresPermissions("supplier:schoolSupplierGoodsRel:add")
    @Log(title = "学校供应商合作商品" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptSchoolSupplierGoodsRel xfptSchoolSupplierGoodsRel) {
            return toAjax(xfptSchoolSupplierGoodsRelService.save(xfptSchoolSupplierGoodsRel));
    }

    /**
     * 修改学校供应商合作商品
     */
    @Operation(summary = "修改学校供应商合作商品")
    @RequiresPermissions("supplier:schoolSupplierGoodsRel:edit")
    @Log(title = "学校供应商合作商品" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptSchoolSupplierGoodsRel xfptSchoolSupplierGoodsRel) {
            return toAjax(xfptSchoolSupplierGoodsRelService.updateById(xfptSchoolSupplierGoodsRel));
    }


    /**
     * 删除学校供应商合作商品
     */
    @Operation(summary = "删除学校供应商合作商品")
    @RequiresPermissions("supplier:schoolSupplierGoodsRel:remove")
    @Log(title = "学校供应商合作商品" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
            return toAjax(xfptSchoolSupplierGoodsRelService.removeByIds(Arrays.asList(ids)));
    }
}
