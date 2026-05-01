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
import com.xfgl.supplier.domain.XfptStudentSizeExt;
import com.xfgl.supplier.service.IXfptStudentSizeExtService;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;

/**
 * 学生尺码扩展Controller
 *
 * @author yinet
 * @date 2026-05-01
 */
@Tag(name="学生尺码扩展管理")
@RestController
@RequestMapping("/studentSizeExt")
public class XfptStudentSizeExtController extends BaseController {
    @Autowired
    private IXfptStudentSizeExtService xfptStudentSizeExtService;

/**
 * 查询学生尺码扩展列表
 */

@Operation(summary = "查询学生尺码扩展列表")
@RequiresPermissions("supplier:studentSizeExt:list")
@GetMapping("/list")
    public TableDataInfo list(XfptStudentSizeExt xfptStudentSizeExt) {
        startPage();
        List<XfptStudentSizeExt> list = xfptStudentSizeExtService.selectXfptStudentSizeExtList(xfptStudentSizeExt);
        return getDataTable(list);
    }

    /**
     * 导出学生尺码扩展列表
     */
    @Operation(summary = "导出学生尺码扩展列表")
    @RequiresPermissions("supplier:studentSizeExt:export")
    @Log(title = "学生尺码扩展" , businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptStudentSizeExt xfptStudentSizeExt) {
        List<XfptStudentSizeExt> list = xfptStudentSizeExtService.selectXfptStudentSizeExtList(xfptStudentSizeExt);
        ExcelUtil<XfptStudentSizeExt> util = new ExcelUtil<XfptStudentSizeExt>(XfptStudentSizeExt. class);
        util.exportExcel(response, list, "学生尺码扩展数据");
    }

    /**
     * 获取学生尺码扩展详细信息
     */
    @Operation(summary = "获取学生尺码扩展详细信息")
    @RequiresPermissions("supplier:studentSizeExt:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
            return success(xfptStudentSizeExtService.getById(id));
    }

    /**
     * 新增学生尺码扩展
     */
    @Operation(summary = "新增学生尺码扩展")
    @RequiresPermissions("supplier:studentSizeExt:add")
    @Log(title = "学生尺码扩展" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptStudentSizeExt xfptStudentSizeExt) {
            return toAjax(xfptStudentSizeExtService.save(xfptStudentSizeExt));
    }

    /**
     * 修改学生尺码扩展
     */
    @Operation(summary = "修改学生尺码扩展")
    @RequiresPermissions("supplier:studentSizeExt:edit")
    @Log(title = "学生尺码扩展" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptStudentSizeExt xfptStudentSizeExt) {
            return toAjax(xfptStudentSizeExtService.updateById(xfptStudentSizeExt));
    }


    /**
     * 删除学生尺码扩展
     */
    @Operation(summary = "删除学生尺码扩展")
    @RequiresPermissions("supplier:studentSizeExt:remove")
    @Log(title = "学生尺码扩展" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
            return toAjax(xfptStudentSizeExtService.removeByIds(Arrays.asList(ids)));
    }
}
