package com.xfgl.school.controller;

import java.util.List;
import java.util.Arrays;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.system.api.domain.SysUser;
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
import com.xfgl.school.domain.XfptProcSummary;
import com.xfgl.school.service.IXfptProcSummaryService;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;

/**
 * 采购汇总信息Controller
 *
 * @author yinet
 * @date 2025-05-09
 */
@Tag(name = "采购汇总信息管理")
@RestController
@RequestMapping("/summary")
public class XfptProcSummaryController extends BaseController {
    @Autowired
    private IXfptProcSummaryService xfptProcSummaryService;

    /**
     * 查询采购汇总信息列表
     */

    @Operation(summary = "查询采购汇总信息列表")
    //@RequiresPermissions("school:summary:list")
    @GetMapping("/list")
    public TableDataInfo list(XfptProcSummary xfptProcSummary) {
        startPage();
        List<XfptProcSummary> list = xfptProcSummaryService.selectXfptProcSummaryList(xfptProcSummary);
        return getDataTable(list);
    }

    /**
     * 导出采购汇总信息列表
     */
    @Operation(summary = "导出采购汇总信息列表")
    //@RequiresPermissions("school:summary:export")
    @Log(title = "采购汇总信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptProcSummary xfptProcSummary) {
        List<XfptProcSummary> list = xfptProcSummaryService.selectXfptProcSummaryList(xfptProcSummary);
        ExcelUtil<XfptProcSummary> util = new ExcelUtil<XfptProcSummary>(XfptProcSummary.class);
        util.exportExcel(response, list, "采购汇总信息数据");
    }

    /**
     * 获取采购汇总信息详细信息
     */
    @Operation(summary = "获取采购汇总信息详细信息")
    //@RequiresPermissions("school:summary:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {


        return success(xfptProcSummaryService.lambdaQuery().eq(XfptProcSummary::getTeamId,id).one());
    }

    /**
     * 新增采购汇总信息
     */
    @Operation(summary = "新增采购汇总信息")
   // @RequiresPermissions("school:summary:add")
    @Log(title = "采购汇总信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptProcSummary xfptProcSummary) {

        SysUser sysUser = SecurityUtils.getLoginUser().getSysUser();
        xfptProcSummary.setAncestors(sysUser.getDept().getAncestors());
        xfptProcSummary.setDeptId(sysUser.getDeptId());
        xfptProcSummary.setSchoolName(sysUser.getNickName());

        return toAjax(xfptProcSummaryService.save(xfptProcSummary));
    }

    /**
     * 修改采购汇总信息
     */
    @Operation(summary = "修改采购汇总信息")
    //@RequiresPermissions("school:summary:edit")
    @Log(title = "采购汇总信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptProcSummary xfptProcSummary) {
        SysUser sysUser = SecurityUtils.getLoginUser().getSysUser();
        xfptProcSummary.setAncestors(sysUser.getDept().getAncestors());
        xfptProcSummary.setDeptId(sysUser.getDeptId());
        xfptProcSummary.setSchoolName(sysUser.getNickName());
        return toAjax(xfptProcSummaryService.saveOrUpdate(xfptProcSummary));
    }


    /**
     * 删除采购汇总信息
     */
    @Operation(summary = "删除采购汇总信息")
    //@RequiresPermissions("school:summary:remove")
    @Log(title = "采购汇总信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(xfptProcSummaryService.removeByIds(Arrays.asList(ids)));
    }
}
