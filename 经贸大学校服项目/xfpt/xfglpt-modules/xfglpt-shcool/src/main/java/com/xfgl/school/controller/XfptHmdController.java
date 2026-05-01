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
import com.xfgl.school.domain.XfptHmd;
import com.xfgl.school.service.IXfptHmdService;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;

/**
 * 黑名单Controller
 *
 * @author yinet
 * @date 2025-10-23
 */
@Tag(name="黑名单管理")
@RestController
@RequestMapping("/hmd")
public class XfptHmdController extends BaseController {
    @Autowired
    private IXfptHmdService xfptHmdService;

/**
 * 查询黑名单列表
 */

@Operation(summary = "查询黑名单列表")
@RequiresPermissions("school:hmd:list")
@GetMapping("/list")
    public TableDataInfo list(XfptHmd xfptHmd) {
        startPage();
        List<XfptHmd> list = xfptHmdService.selectXfptHmdList(xfptHmd);
        return getDataTable(list);
    }

    /**
     * 导出黑名单列表
     */
    @Operation(summary = "导出黑名单列表")
    @RequiresPermissions("school:hmd:export")
    @Log(title = "黑名单" , businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptHmd xfptHmd) {
        List<XfptHmd> list = xfptHmdService.selectXfptHmdList(xfptHmd);
        ExcelUtil<XfptHmd> util = new ExcelUtil<XfptHmd>(XfptHmd. class);
        util.exportExcel(response, list, "黑名单数据");
    }

    /**
     * 获取黑名单详细信息
     */
    @Operation(summary = "获取黑名单详细信息")
    @RequiresPermissions("school:hmd:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
            return success(xfptHmdService.getById(id));
    }

    /**
     * 新增黑名单
     */
    @Operation(summary = "新增黑名单")
    @RequiresPermissions("school:hmd:add")
    @Log(title = "黑名单" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptHmd xfptHmd) {
            return toAjax(xfptHmdService.save(xfptHmd));
    }

    /**
     * 修改黑名单
     */
    @Operation(summary = "修改黑名单")
    @RequiresPermissions("school:hmd:edit")
    @Log(title = "黑名单" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptHmd xfptHmd) {
            return toAjax(xfptHmdService.updateById(xfptHmd));
    }


    /**
     * 删除黑名单
     */
    @Operation(summary = "删除黑名单")
    @RequiresPermissions("school:hmd:remove")
    @Log(title = "黑名单" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
            return toAjax(xfptHmdService.removeByIds(Arrays.asList(ids)));
    }
}
