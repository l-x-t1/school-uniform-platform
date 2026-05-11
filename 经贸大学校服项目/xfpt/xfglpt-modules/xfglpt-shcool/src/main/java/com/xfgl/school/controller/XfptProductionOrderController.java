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
import com.xfgl.school.domain.XfptProductionOrder;
import com.xfgl.school.service.IXfptProductionOrderService;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;

/**
 * 生产单Controller
 *
 * @author yinet
 * @date 2026-05-04
 */
@Tag(name="生产单管理")
@RestController
@RequestMapping("/productionOrder")
public class XfptProductionOrderController extends BaseController {
    @Autowired
    private IXfptProductionOrderService xfptProductionOrderService;

/**
 * 查询生产单列表
 */

@Operation(summary = "查询生产单列表")
@RequiresPermissions("school:productionOrder:list")
@GetMapping("/list")
    public TableDataInfo list(XfptProductionOrder xfptProductionOrder) {
        startPage();
        List<XfptProductionOrder> list = xfptProductionOrderService.selectXfptProductionOrderList(xfptProductionOrder);
        return getDataTable(list);
    }

    /**
     * 导出生产单列表
     */
    @Operation(summary = "导出生产单列表")
    @RequiresPermissions("school:productionOrder:export")
    @Log(title = "生产单" , businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptProductionOrder xfptProductionOrder) {
        List<XfptProductionOrder> list = xfptProductionOrderService.selectXfptProductionOrderList(xfptProductionOrder);
        ExcelUtil<XfptProductionOrder> util = new ExcelUtil<XfptProductionOrder>(XfptProductionOrder. class);
        util.exportExcel(response, list, "生产单数据");
    }

    /**
     * 获取生产单详细信息
     */
    @Operation(summary = "获取生产单详细信息")
    @RequiresPermissions("school:productionOrder:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
            return success(xfptProductionOrderService.selectXfptProductionOrderById(id));
    }

    /**
     * 新增生产单
     */
    @Operation(summary = "新增生产单")
    @RequiresPermissions("school:productionOrder:add")
    @Log(title = "生产单" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptProductionOrder xfptProductionOrder) {
            return toAjax(xfptProductionOrderService.insertXfptProductionOrder(xfptProductionOrder));
    }

    /**
     * 根据订单生成生产单
     */
    @Operation(summary = "根据订单生成生产单")
    @RequiresPermissions("school:productionOrder:add")
    @Log(title = "生产单", businessType = BusinessType.INSERT)
    @PostMapping("/generateFromOrder/{orderId}")
    public AjaxResult generateFromOrder(@PathVariable("orderId") Long orderId) {
        try {
            return success(xfptProductionOrderService.generateFromOrder(orderId));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return error(e.getMessage());
        }
    }

    /**
     * 开始生产
     */
    @Operation(summary = "开始生产")
    @RequiresPermissions("school:productionOrder:edit")
    @Log(title = "生产单", businessType = BusinessType.UPDATE)
    @PutMapping("/start/{id}")
    public AjaxResult startProduction(@PathVariable("id") Long id) {
        try {
            return toAjax(xfptProductionOrderService.startProduction(id));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return error(e.getMessage());
        }
    }

    /**
     * 完成生产
     */
    @Operation(summary = "完成生产")
    @RequiresPermissions("school:productionOrder:edit")
    @Log(title = "生产单", businessType = BusinessType.UPDATE)
    @PutMapping("/finish/{id}")
    public AjaxResult finishProduction(@PathVariable("id") Long id) {
        try {
            return toAjax(xfptProductionOrderService.finishProduction(id));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return error(e.getMessage());
        }
    }

    /**
     * 修改生产单
     */
    @Operation(summary = "修改生产单")
    @RequiresPermissions("school:productionOrder:edit")
    @Log(title = "生产单" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptProductionOrder xfptProductionOrder) {
            return toAjax(xfptProductionOrderService.updateXfptProductionOrder(xfptProductionOrder));
    }


    /**
     * 删除生产单
     */
    @Operation(summary = "删除生产单")
    @RequiresPermissions("school:productionOrder:remove")
    @Log(title = "生产单" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
            return toAjax(xfptProductionOrderService.deleteXfptProductionOrderByIds(ids));
    }
}
