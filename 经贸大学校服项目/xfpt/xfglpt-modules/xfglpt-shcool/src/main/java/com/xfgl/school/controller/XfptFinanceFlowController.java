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
import com.xfgl.school.domain.XfptFinanceFlow;
import com.xfgl.school.service.IXfptFinanceFlowService;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;

/**
 * 资金流水Controller
 *
 * @author yinet
 * @date 2026-05-04
 */
@Tag(name="资金流水管理")
@RestController
@RequestMapping("/financeFlow")
public class XfptFinanceFlowController extends BaseController {
    @Autowired
    private IXfptFinanceFlowService xfptFinanceFlowService;

/**
 * 查询资金流水列表
 */

@Operation(summary = "查询资金流水列表")
@RequiresPermissions("school:financeFlow:list")
@GetMapping("/list")
    public TableDataInfo list(XfptFinanceFlow xfptFinanceFlow) {
        startPage();
        List<XfptFinanceFlow> list = xfptFinanceFlowService.selectXfptFinanceFlowList(xfptFinanceFlow);
        return getDataTable(list);
    }

    /**
     * 导出资金流水列表
     */
    @Operation(summary = "导出资金流水列表")
    @RequiresPermissions("school:financeFlow:export")
    @Log(title = "资金流水" , businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptFinanceFlow xfptFinanceFlow) {
        List<XfptFinanceFlow> list = xfptFinanceFlowService.selectXfptFinanceFlowList(xfptFinanceFlow);
        ExcelUtil<XfptFinanceFlow> util = new ExcelUtil<XfptFinanceFlow>(XfptFinanceFlow. class);
        util.exportExcel(response, list, "资金流水数据");
    }

    /**
     * 获取资金流水详细信息
     */
    @Operation(summary = "获取资金流水详细信息")
    @RequiresPermissions("school:financeFlow:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
            return success(xfptFinanceFlowService.getById(id));
    }

    /**
     * 新增资金流水
     */
    @Operation(summary = "新增资金流水")
    @RequiresPermissions("school:financeFlow:add")
    @Log(title = "资金流水" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptFinanceFlow xfptFinanceFlow) {
            return toAjax(xfptFinanceFlowService.save(xfptFinanceFlow));
    }

    /**
     * 修改资金流水
     */
    @Operation(summary = "修改资金流水")
    @RequiresPermissions("school:financeFlow:edit")
    @Log(title = "资金流水" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptFinanceFlow xfptFinanceFlow) {
            return toAjax(xfptFinanceFlowService.updateById(xfptFinanceFlow));
    }


    /**
     * 删除资金流水
     */
    @Operation(summary = "删除资金流水")
    @RequiresPermissions("school:financeFlow:remove")
    @Log(title = "资金流水" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
            return toAjax(xfptFinanceFlowService.removeByIds(Arrays.asList(ids)));
    }
}
