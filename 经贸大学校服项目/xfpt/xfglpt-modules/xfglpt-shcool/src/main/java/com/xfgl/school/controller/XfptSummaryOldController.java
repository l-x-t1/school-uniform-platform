package com.xfgl.school.controller;

import java.util.List;
import java.util.Arrays;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.system.api.domain.SysDept;
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
import com.xfgl.school.domain.XfptSummaryOld;
import com.xfgl.school.service.IXfptSummaryOldService;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;

/**
 * 采购历史数据Controller
 *
 * @author yinet
 * @date 2025-06-03
 */
@Tag(name = "采购历史数据管理")
@RestController
@RequestMapping("/oldData")
public class XfptSummaryOldController extends BaseController {
    @Autowired
    private IXfptSummaryOldService xfptSummaryOldService;

    /**
     * 查询采购历史数据列表
     */

    @Operation(summary = "查询采购历史数据列表")
    @RequiresPermissions("school:oldData:list")
    @GetMapping("/list")
    public TableDataInfo list(XfptSummaryOld xfptSummaryOld) {
        SysDept dept = SecurityUtils.getLoginUser().getSysUser().getDept();
        xfptSummaryOld.setDeptId(dept.getDeptId());
        startPage();
        List<XfptSummaryOld> list = xfptSummaryOldService.selectXfptSummaryOldList(xfptSummaryOld);
        return getDataTable(list);
    }

    /**
     * 导出采购历史数据列表
     */
    @Operation(summary = "导出采购历史数据列表")
    @RequiresPermissions("school:oldData:export")
    @Log(title = "采购历史数据", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptSummaryOld xfptSummaryOld) {
        List<XfptSummaryOld> list = xfptSummaryOldService.selectXfptSummaryOldList(xfptSummaryOld);
        ExcelUtil<XfptSummaryOld> util = new ExcelUtil<XfptSummaryOld>(XfptSummaryOld.class);
        util.exportExcel(response, list, "采购历史数据数据");
    }

    /**
     * 获取采购历史数据详细信息
     */
    @Operation(summary = "获取采购历史数据详细信息")
    @RequiresPermissions("school:oldData:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(xfptSummaryOldService.getById(id));
    }

    /**
     * 新增采购历史数据
     */
    @Operation(summary = "新增采购历史数据")
    @RequiresPermissions("school:oldData:add")
    @Log(title = "采购历史数据", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptSummaryOld xfptSummaryOld) {
        SysDept dept = SecurityUtils.getLoginUser().getSysUser().getDept();
        xfptSummaryOld.setAncestors(dept.getAncestors());
        xfptSummaryOld.setDeptId(dept.getDeptId());

        return toAjax(xfptSummaryOldService.save(xfptSummaryOld));
    }

    /**
     * 修改采购历史数据
     */
    @Operation(summary = "修改采购历史数据")
    @RequiresPermissions("school:oldData:edit")
    @Log(title = "采购历史数据", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptSummaryOld xfptSummaryOld) {
        return toAjax(xfptSummaryOldService.updateById(xfptSummaryOld));
    }


    /**
     * 删除采购历史数据
     */
    @Operation(summary = "删除采购历史数据")
    @RequiresPermissions("school:oldData:remove")
    @Log(title = "采购历史数据", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(xfptSummaryOldService.removeByIds(Arrays.asList(ids)));
    }
}
