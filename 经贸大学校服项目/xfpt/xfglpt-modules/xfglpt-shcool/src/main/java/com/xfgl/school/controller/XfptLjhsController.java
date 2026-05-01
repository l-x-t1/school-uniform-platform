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
import com.xfgl.school.domain.XfptLjhs;
import com.xfgl.school.service.IXfptLjhsService;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;

/**
 * 利旧回收Controller
 *
 * @author yinet
 * @date 2025-09-15
 */
@Tag(name = "利旧回收管理")
@RestController
@RequestMapping("/ljhs")
public class XfptLjhsController extends BaseController {
    @Autowired
    private IXfptLjhsService xfptLjhsService;

    /**
     * 查询利旧回收列表
     */

    @Operation(summary = "查询利旧回收列表")
    @GetMapping("/list")
    public TableDataInfo list(XfptLjhs xfptLjhs) {
        startPage();
        List<XfptLjhs> list = xfptLjhsService.selectXfptLjhsList(xfptLjhs);
        return getDataTable(list);
    }

    /**
     * 导出利旧回收列表
     */
    @Operation(summary = "导出利旧回收列表")
    @Log(title = "利旧回收", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptLjhs xfptLjhs) {
        List<XfptLjhs> list = xfptLjhsService.selectXfptLjhsList(xfptLjhs);
        ExcelUtil<XfptLjhs> util = new ExcelUtil<XfptLjhs>(XfptLjhs.class);
        util.exportExcel(response, list, "利旧回收数据");
    }

    /**
     * 获取利旧回收详细信息
     */
    @Operation(summary = "获取利旧回收详细信息")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(xfptLjhsService.getById(id));
    }

    /**
     * 新增利旧回收
     */
    @Operation(summary = "新增利旧回收")
    @Log(title = "利旧回收", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptLjhs xfptLjhs) {
        return toAjax(xfptLjhsService.save(xfptLjhs));
    }

    /**
     * 修改利旧回收
     */
    @Operation(summary = "修改利旧回收")
    @Log(title = "利旧回收", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptLjhs xfptLjhs) {
        return toAjax(xfptLjhsService.updateById(xfptLjhs));
    }


    /**
     * 删除利旧回收
     */
    @Operation(summary = "删除利旧回收")
    @Log(title = "利旧回收", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(xfptLjhsService.removeByIds(Arrays.asList(ids)));
    }
}
