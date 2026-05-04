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
import com.xfgl.school.domain.XfptDeliveryOrderItem;
import com.xfgl.school.service.IXfptDeliveryOrderItemService;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;

/**
 * 发货单明细Controller
 *
 * @author yinet
 * @date 2026-05-04
 */
@Tag(name="发货单明细管理")
@RestController
@RequestMapping("/deliveryOrderItem")
public class XfptDeliveryOrderItemController extends BaseController {
    @Autowired
    private IXfptDeliveryOrderItemService xfptDeliveryOrderItemService;

/**
 * 查询发货单明细列表
 */

@Operation(summary = "查询发货单明细列表")
@RequiresPermissions("school:deliveryOrderItem:list")
@GetMapping("/list")
    public TableDataInfo list(XfptDeliveryOrderItem xfptDeliveryOrderItem) {
        startPage();
        List<XfptDeliveryOrderItem> list = xfptDeliveryOrderItemService.selectXfptDeliveryOrderItemList(xfptDeliveryOrderItem);
        return getDataTable(list);
    }

    /**
     * 导出发货单明细列表
     */
    @Operation(summary = "导出发货单明细列表")
    @RequiresPermissions("school:deliveryOrderItem:export")
    @Log(title = "发货单明细" , businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptDeliveryOrderItem xfptDeliveryOrderItem) {
        List<XfptDeliveryOrderItem> list = xfptDeliveryOrderItemService.selectXfptDeliveryOrderItemList(xfptDeliveryOrderItem);
        ExcelUtil<XfptDeliveryOrderItem> util = new ExcelUtil<XfptDeliveryOrderItem>(XfptDeliveryOrderItem. class);
        util.exportExcel(response, list, "发货单明细数据");
    }

    /**
     * 获取发货单明细详细信息
     */
    @Operation(summary = "获取发货单明细详细信息")
    @RequiresPermissions("school:deliveryOrderItem:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
            return success(xfptDeliveryOrderItemService.getById(id));
    }

    /**
     * 新增发货单明细
     */
    @Operation(summary = "新增发货单明细")
    @RequiresPermissions("school:deliveryOrderItem:add")
    @Log(title = "发货单明细" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptDeliveryOrderItem xfptDeliveryOrderItem) {
            return toAjax(xfptDeliveryOrderItemService.save(xfptDeliveryOrderItem));
    }

    /**
     * 修改发货单明细
     */
    @Operation(summary = "修改发货单明细")
    @RequiresPermissions("school:deliveryOrderItem:edit")
    @Log(title = "发货单明细" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptDeliveryOrderItem xfptDeliveryOrderItem) {
            return toAjax(xfptDeliveryOrderItemService.updateById(xfptDeliveryOrderItem));
    }


    /**
     * 删除发货单明细
     */
    @Operation(summary = "删除发货单明细")
    @RequiresPermissions("school:deliveryOrderItem:remove")
    @Log(title = "发货单明细" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
            return toAjax(xfptDeliveryOrderItemService.removeByIds(Arrays.asList(ids)));
    }
}
