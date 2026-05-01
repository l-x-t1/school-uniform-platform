package com.xfgl.system.controller;

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
import com.xfgl.system.domain.XfptCategoryNotice;
import com.xfgl.system.service.IXfptCategoryNoticeService;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * 栏目分类Controller
 *
 * @author yinet
 * @date 2025-05-06
 */
@Tag(name = "栏目分类管理")
@RestController
@RequestMapping("/category")
public class XfptCategoryNoticeController extends BaseController {
    @Autowired
    private IXfptCategoryNoticeService xfptCategoryNoticeService;

    /**
     * 查询栏目分类列表
     */

    @Operation(summary = "查询栏目分类列表")
    @RequiresPermissions("system:category:list")
    @GetMapping("/list")
    public AjaxResult list(XfptCategoryNotice xfptCategoryNotice) {
        List<XfptCategoryNotice> list = xfptCategoryNoticeService.selectXfptCategoryNoticeList(xfptCategoryNotice);
        return success(list);
    }
    /**
     * 查询栏目分类列表
     */

    @Operation(summary = "查询栏目分类列表")
    @GetMapping("/listNotCGGG")
    public AjaxResult listNotCGGG(XfptCategoryNotice xfptCategoryNotice) {
        List<XfptCategoryNotice> list = xfptCategoryNoticeService.selectXfptCategoryNoticeList2(xfptCategoryNotice);
        return success(list);
    }
    /**
     * 导出栏目分类列表
     */
    @Operation(summary = "导出栏目分类列表")
    @RequiresPermissions("system:category:export")
    @Log(title = "栏目分类", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptCategoryNotice xfptCategoryNotice) {
        List<XfptCategoryNotice> list = xfptCategoryNoticeService.selectXfptCategoryNoticeList(xfptCategoryNotice);
        ExcelUtil<XfptCategoryNotice> util = new ExcelUtil<XfptCategoryNotice>(XfptCategoryNotice.class);
        util.exportExcel(response, list, "栏目分类数据");
    }

    /**
     * 获取栏目分类详细信息
     */
    @Operation(summary = "获取栏目分类详细信息")
    @RequiresPermissions("system:category:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(xfptCategoryNoticeService.getById(id));
    }

    /**
     * 新增栏目分类
     */
    @Operation(summary = "新增栏目分类")
    @RequiresPermissions("system:category:add")
    @Log(title = "栏目分类", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptCategoryNotice xfptCategoryNotice) {
        return toAjax(xfptCategoryNoticeService.save(xfptCategoryNotice));
    }

    /**
     * 修改栏目分类
     */
    @Operation(summary = "修改栏目分类")
    @RequiresPermissions("system:category:edit")
    @Log(title = "栏目分类", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptCategoryNotice xfptCategoryNotice) {
        return toAjax(xfptCategoryNoticeService.updateById(xfptCategoryNotice));
    }


    /**
     * 删除栏目分类
     */
    @Operation(summary = "删除栏目分类")
    @RequiresPermissions("system:category:remove")
    @Log(title = "栏目分类", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(xfptCategoryNoticeService.removeByIds(Arrays.asList(ids)));
    }
}
