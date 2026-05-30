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
import com.xfgl.school.domain.XfptOrderItem;
import com.xfgl.school.service.IXfptOrderItemService;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;

/**
 * 订单明细Controller
 *
 * @author yinet
 * @date 2026-05-04
 */
@Tag(name="订单明细管理")
@RestController
@RequestMapping({"/orderItem", "/school/orderItem", "/xfglpt-school/orderItem"})
public class XfptOrderItemController extends BaseController {
    @Autowired
    private IXfptOrderItemService xfptOrderItemService;

/**
 * 查询订单明细列表
 */

@Operation(summary = "查询订单明细列表")
@RequiresPermissions("school:orderItem:list")
@GetMapping("/list")
    public TableDataInfo list(XfptOrderItem xfptOrderItem) {
        startPage();
        List<XfptOrderItem> list = xfptOrderItemService.selectXfptOrderItemList(xfptOrderItem);
        return getDataTable(list);
    }

    /**
     * 导出订单明细列表
     */
    @Operation(summary = "导出订单明细列表")
    @RequiresPermissions("school:orderItem:export")
    @Log(title = "订单明细" , businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptOrderItem xfptOrderItem) {
        List<XfptOrderItem> list = xfptOrderItemService.selectXfptOrderItemList(xfptOrderItem);
        ExcelUtil<XfptOrderItem> util = new ExcelUtil<XfptOrderItem>(XfptOrderItem. class);
        util.exportExcel(response, list, "订单明细数据");
    }

    /**
     * 获取订单明细详细信息
     */
    @Operation(summary = "获取订单明细详细信息")
    @RequiresPermissions("school:orderItem:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
            return success(xfptOrderItemService.getById(id));
    }

    /**
     * 新增订单明细
     */
    @Operation(summary = "新增订单明细")
    @RequiresPermissions("school:orderItem:add")
    @Log(title = "订单明细" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptOrderItem xfptOrderItem) {
            return toAjax(xfptOrderItemService.save(xfptOrderItem));
    }

    /**
     * 修改订单明细
     */
    @Operation(summary = "修改订单明细")
    @RequiresPermissions("school:orderItem:edit")
    @Log(title = "订单明细" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptOrderItem xfptOrderItem) {
            return toAjax(xfptOrderItemService.updateById(xfptOrderItem));
    }


    /**
     * 删除订单明细
     */
    @Operation(summary = "删除订单明细")
    @RequiresPermissions("school:orderItem:remove")
    @Log(title = "订单明细" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
            return toAjax(xfptOrderItemService.removeByIds(Arrays.asList(ids)));
    }
}
