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
import com.xfgl.supplier.domain.XfptCartItem;
import com.xfgl.supplier.service.IXfptCartItemService;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;

/**
 * 购物车明细Controller
 *
 * @author yinet
 * @date 2026-05-01
 */
@Tag(name="购物车明细管理")
@RestController
@RequestMapping("/cart_item")
public class XfptCartItemController extends BaseController {
    @Autowired
    private IXfptCartItemService xfptCartItemService;

/**
 * 查询购物车明细列表
 */

@Operation(summary = "查询购物车明细列表")
@RequiresPermissions("supplier:cart_item:list")
@GetMapping("/list")
    public TableDataInfo list(XfptCartItem xfptCartItem) {
        startPage();
        List<XfptCartItem> list = xfptCartItemService.selectXfptCartItemList(xfptCartItem);
        return getDataTable(list);
    }

    /**
     * 导出购物车明细列表
     */
    @Operation(summary = "导出购物车明细列表")
    @RequiresPermissions("supplier:cart_item:export")
    @Log(title = "购物车明细" , businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptCartItem xfptCartItem) {
        List<XfptCartItem> list = xfptCartItemService.selectXfptCartItemList(xfptCartItem);
        ExcelUtil<XfptCartItem> util = new ExcelUtil<XfptCartItem>(XfptCartItem. class);
        util.exportExcel(response, list, "购物车明细数据");
    }

    /**
     * 获取购物车明细详细信息
     */
    @Operation(summary = "获取购物车明细详细信息")
    @RequiresPermissions("supplier:cart_item:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
            return success(xfptCartItemService.getById(id));
    }

    /**
     * 新增购物车明细
     */
    @Operation(summary = "新增购物车明细")
    @RequiresPermissions("supplier:cart_item:add")
    @Log(title = "购物车明细" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptCartItem xfptCartItem) {
            return toAjax(xfptCartItemService.save(xfptCartItem));
    }

    /**
     * 修改购物车明细
     */
    @Operation(summary = "修改购物车明细")
    @RequiresPermissions("supplier:cart_item:edit")
    @Log(title = "购物车明细" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptCartItem xfptCartItem) {
            return toAjax(xfptCartItemService.updateById(xfptCartItem));
    }


    /**
     * 删除购物车明细
     */
    @Operation(summary = "删除购物车明细")
    @RequiresPermissions("supplier:cart_item:remove")
    @Log(title = "购物车明细" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
            return toAjax(xfptCartItemService.removeByIds(Arrays.asList(ids)));
    }
}
