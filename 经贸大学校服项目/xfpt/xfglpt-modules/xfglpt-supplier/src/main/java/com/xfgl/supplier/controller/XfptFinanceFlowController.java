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
import com.xfgl.supplier.domain.XfptFinanceFlow;
import com.xfgl.supplier.service.IXfptFinanceFlowService;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;

/**
 * 财务流水Controller
 *
 * @author yinet
 * @date 2026-05-01
 */
@Tag(name="财务流水管理")
@RestController
@RequestMapping("/finance_flow")
public class XfptFinanceFlowController extends BaseController {
    @Autowired
    private IXfptFinanceFlowService xfptFinanceFlowService;

/**
 * 查询财务流水列表
 */

@Operation(summary = "查询财务流水列表")
@RequiresPermissions("supplier:finance_flow:list")
@GetMapping("/list")
    public TableDataInfo list(XfptFinanceFlow xfptFinanceFlow) {
        startPage();
        List<XfptFinanceFlow> list = xfptFinanceFlowService.selectXfptFinanceFlowList(xfptFinanceFlow);
        return getDataTable(list);
    }

    /**
     * 导出财务流水列表
     */
    @Operation(summary = "导出财务流水列表")
    @RequiresPermissions("supplier:finance_flow:export")
    @Log(title = "财务流水" , businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptFinanceFlow xfptFinanceFlow) {
        List<XfptFinanceFlow> list = xfptFinanceFlowService.selectXfptFinanceFlowList(xfptFinanceFlow);
        ExcelUtil<XfptFinanceFlow> util = new ExcelUtil<XfptFinanceFlow>(XfptFinanceFlow. class);
        util.exportExcel(response, list, "财务流水数据");
    }

    /**
     * 获取财务流水详细信息
     */
    @Operation(summary = "获取财务流水详细信息")
    @RequiresPermissions("supplier:finance_flow:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
            return success(xfptFinanceFlowService.getById(id));
    }

    /**
     * 新增财务流水
     */
    @Operation(summary = "新增财务流水")
    @RequiresPermissions("supplier:finance_flow:add")
    @Log(title = "财务流水" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptFinanceFlow xfptFinanceFlow) {
            return toAjax(xfptFinanceFlowService.save(xfptFinanceFlow));
    }

    /**
     * 修改财务流水
     */
    @Operation(summary = "修改财务流水")
    @RequiresPermissions("supplier:finance_flow:edit")
    @Log(title = "财务流水" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptFinanceFlow xfptFinanceFlow) {
            return toAjax(xfptFinanceFlowService.updateById(xfptFinanceFlow));
    }


    /**
     * 删除财务流水
     */
    @Operation(summary = "删除财务流水")
    @RequiresPermissions("supplier:finance_flow:remove")
    @Log(title = "财务流水" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
            return toAjax(xfptFinanceFlowService.removeByIds(Arrays.asList(ids)));
    }
}
