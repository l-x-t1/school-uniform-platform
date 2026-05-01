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
import com.xfgl.school.domain.XfptSimUser;
import com.xfgl.school.service.IXfptSimUserService;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;

/**
 * 石i民用户Controller
 *
 * @author yinet
 * @date 2025-05-13
 */
@Tag(name = "石i民用户管理")
@RestController
@RequestMapping("/schoolSimUser")
public class XfptSimUserController extends BaseController {
    @Autowired
    private IXfptSimUserService xfptSimUserService;

    /**
     * 查询石i民用户列表
     */

    @Operation(summary = "查询石i民用户列表")
    @RequiresPermissions("school:schoolSimUser:list")
    @GetMapping("/list")
    public TableDataInfo list(XfptSimUser xfptSimUser) {
        startPage();
        List<XfptSimUser> list = xfptSimUserService.selectXfptSimUserList(xfptSimUser);
        return getDataTable(list);
    }

    /**
     * 导出石i民用户列表
     */
    @Operation(summary = "导出石i民用户列表")
    @RequiresPermissions("school:schoolSimUser:export")
    @Log(title = "石i民用户", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptSimUser xfptSimUser) {
        List<XfptSimUser> list = xfptSimUserService.selectXfptSimUserList(xfptSimUser);
        ExcelUtil<XfptSimUser> util = new ExcelUtil<XfptSimUser>(XfptSimUser.class);
        util.exportExcel(response, list, "石i民用户数据");
    }

    /**
     * 获取石i民用户详细信息
     */
    @Operation(summary = "获取石i民用户详细信息")
    @RequiresPermissions("school:schoolSimUser:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(xfptSimUserService.getById(id));
    }

    /**
     * 新增石i民用户
     */
    @Operation(summary = "新增石i民用户")
    @RequiresPermissions("school:schoolSimUser:add")
    @Log(title = "石i民用户", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptSimUser xfptSimUser) {
        return toAjax(xfptSimUserService.save(xfptSimUser));
    }

    /**
     * 修改石i民用户
     */
    @Operation(summary = "修改石i民用户")
    @RequiresPermissions("school:schoolSimUser:edit")
    @Log(title = "石i民用户", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptSimUser xfptSimUser) {
        return toAjax(xfptSimUserService.updateById(xfptSimUser));
    }


    /**
     * 删除石i民用户
     */
    @Operation(summary = "删除石i民用户")
    @RequiresPermissions("school:schoolSimUser:remove")
    @Log(title = "石i民用户", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(xfptSimUserService.removeByIds(Arrays.asList(ids)));
    }
}
