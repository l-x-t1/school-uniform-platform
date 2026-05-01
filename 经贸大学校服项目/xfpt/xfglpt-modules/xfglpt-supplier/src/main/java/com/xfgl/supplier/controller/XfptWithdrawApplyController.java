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
import com.xfgl.supplier.domain.XfptWithdrawApply;
import com.xfgl.supplier.service.IXfptWithdrawApplyService;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;

/**
 * 提现申请Controller
 *
 * @author yinet
 * @date 2026-05-01
 */
@Tag(name="提现申请管理")
@RestController
@RequestMapping("/withdraw_apply")
public class XfptWithdrawApplyController extends BaseController {
    @Autowired
    private IXfptWithdrawApplyService xfptWithdrawApplyService;

/**
 * 查询提现申请列表
 */

@Operation(summary = "查询提现申请列表")
@RequiresPermissions("supplier:withdraw_apply:list")
@GetMapping("/list")
    public TableDataInfo list(XfptWithdrawApply xfptWithdrawApply) {
        startPage();
        List<XfptWithdrawApply> list = xfptWithdrawApplyService.selectXfptWithdrawApplyList(xfptWithdrawApply);
        return getDataTable(list);
    }

    /**
     * 导出提现申请列表
     */
    @Operation(summary = "导出提现申请列表")
    @RequiresPermissions("supplier:withdraw_apply:export")
    @Log(title = "提现申请" , businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptWithdrawApply xfptWithdrawApply) {
        List<XfptWithdrawApply> list = xfptWithdrawApplyService.selectXfptWithdrawApplyList(xfptWithdrawApply);
        ExcelUtil<XfptWithdrawApply> util = new ExcelUtil<XfptWithdrawApply>(XfptWithdrawApply. class);
        util.exportExcel(response, list, "提现申请数据");
    }

    /**
     * 获取提现申请详细信息
     */
    @Operation(summary = "获取提现申请详细信息")
    @RequiresPermissions("supplier:withdraw_apply:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
            return success(xfptWithdrawApplyService.getById(id));
    }

    /**
     * 新增提现申请
     */
    @Operation(summary = "新增提现申请")
    @RequiresPermissions("supplier:withdraw_apply:add")
    @Log(title = "提现申请" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptWithdrawApply xfptWithdrawApply) {
            return toAjax(xfptWithdrawApplyService.save(xfptWithdrawApply));
    }

    /**
     * 修改提现申请
     */
    @Operation(summary = "修改提现申请")
    @RequiresPermissions("supplier:withdraw_apply:edit")
    @Log(title = "提现申请" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptWithdrawApply xfptWithdrawApply) {
            return toAjax(xfptWithdrawApplyService.updateById(xfptWithdrawApply));
    }


    /**
     * 删除提现申请
     */
    @Operation(summary = "删除提现申请")
    @RequiresPermissions("supplier:withdraw_apply:remove")
    @Log(title = "提现申请" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
            return toAjax(xfptWithdrawApplyService.removeByIds(Arrays.asList(ids)));
    }
}
