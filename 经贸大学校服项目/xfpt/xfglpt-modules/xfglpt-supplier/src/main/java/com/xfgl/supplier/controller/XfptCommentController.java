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
import com.xfgl.supplier.domain.XfptComment;
import com.xfgl.supplier.service.IXfptCommentService;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;

/**
 * 订单评价Controller
 *
 * @author yinet
 * @date 2026-05-01
 */
@Tag(name="订单评价管理")
@RestController
@RequestMapping("/comment")
public class XfptCommentController extends BaseController {
    @Autowired
    private IXfptCommentService xfptCommentService;

/**
 * 查询订单评价列表
 */

@Operation(summary = "查询订单评价列表")
@RequiresPermissions("supplier:comment:list")
@GetMapping("/list")
    public TableDataInfo list(XfptComment xfptComment) {
        startPage();
        List<XfptComment> list = xfptCommentService.selectXfptCommentList(xfptComment);
        return getDataTable(list);
    }

    /**
     * 导出订单评价列表
     */
    @Operation(summary = "导出订单评价列表")
    @RequiresPermissions("supplier:comment:export")
    @Log(title = "订单评价" , businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptComment xfptComment) {
        List<XfptComment> list = xfptCommentService.selectXfptCommentList(xfptComment);
        ExcelUtil<XfptComment> util = new ExcelUtil<XfptComment>(XfptComment. class);
        util.exportExcel(response, list, "订单评价数据");
    }

    /**
     * 获取订单评价详细信息
     */
    @Operation(summary = "获取订单评价详细信息")
    @RequiresPermissions("supplier:comment:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
            return success(xfptCommentService.getById(id));
    }

    /**
     * 新增订单评价
     */
    @Operation(summary = "新增订单评价")
    @RequiresPermissions("supplier:comment:add")
    @Log(title = "订单评价" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptComment xfptComment) {
            return toAjax(xfptCommentService.save(xfptComment));
    }

    /**
     * 修改订单评价
     */
    @Operation(summary = "修改订单评价")
    @RequiresPermissions("supplier:comment:edit")
    @Log(title = "订单评价" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptComment xfptComment) {
            return toAjax(xfptCommentService.updateById(xfptComment));
    }


    /**
     * 删除订单评价
     */
    @Operation(summary = "删除订单评价")
    @RequiresPermissions("supplier:comment:remove")
    @Log(title = "订单评价" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
            return toAjax(xfptCommentService.removeByIds(Arrays.asList(ids)));
    }
}
