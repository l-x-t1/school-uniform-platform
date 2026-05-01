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
import com.xfgl.school.domain.XfptReportModeOper;
import com.xfgl.school.service.IXfptReportModeOperService;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;

/**
 * 确定选购模式操作记录Controller
 *
 * @author yinet
 * @date 2025-04-24
 */
@Tag(name="确定选购模式操作记录管理")
@RestController
@RequestMapping("/oper")
public class XfptReportModeOperController extends BaseController {
    @Autowired
    private IXfptReportModeOperService xfptReportModeOperService;

/**
 * 查询确定选购模式操作记录列表
 */

@Operation(summary = "查询确定选购模式操作记录列表")
@RequiresPermissions("school:oper:list")
@GetMapping("/list")
    public TableDataInfo list(XfptReportModeOper xfptReportModeOper) {
        startPage();
        List<XfptReportModeOper> list = xfptReportModeOperService.selectXfptReportModeOperList(xfptReportModeOper);
        return getDataTable(list);
    }

    /**
     * 导出确定选购模式操作记录列表
     */
    @Operation(summary = "导出确定选购模式操作记录列表")
   // @RequiresPermissions("school:oper:export")
    @Log(title = "确定选购模式操作记录" , businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptReportModeOper xfptReportModeOper) {
        List<XfptReportModeOper> list = xfptReportModeOperService.selectXfptReportModeOperList(xfptReportModeOper);
        ExcelUtil<XfptReportModeOper> util = new ExcelUtil<XfptReportModeOper>(XfptReportModeOper. class);
        util.exportExcel(response, list, "确定选购模式操作记录数据");
    }

    /**
     * 获取确定选购模式操作记录详细信息
     */
    @Operation(summary = "获取确定选购模式操作记录详细信息")
    //@RequiresPermissions("school:oper:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
            return success(xfptReportModeOperService.getById(id));
    }

    /**
     * 新增确定选购模式操作记录
     */
    @Operation(summary = "新增确定选购模式操作记录")
    //@RequiresPermissions("school:oper:add")
    @Log(title = "确定选购模式操作记录" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptReportModeOper xfptReportModeOper) {
            return toAjax(xfptReportModeOperService.save(xfptReportModeOper));
    }

    /**
     * 修改确定选购模式操作记录
     */
    @Operation(summary = "修改确定选购模式操作记录")
   // @RequiresPermissions("school:oper:edit")
    @Log(title = "确定选购模式操作记录" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptReportModeOper xfptReportModeOper) {
            return toAjax(xfptReportModeOperService.updateById(xfptReportModeOper));
    }


    /**
     * 删除确定选购模式操作记录
     */
    @Operation(summary = "删除确定选购模式操作记录")
   // @RequiresPermissions("school:oper:remove")
    @Log(title = "确定选购模式操作记录" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
            return toAjax(xfptReportModeOperService.removeByIds(Arrays.asList(ids)));
    }
}
