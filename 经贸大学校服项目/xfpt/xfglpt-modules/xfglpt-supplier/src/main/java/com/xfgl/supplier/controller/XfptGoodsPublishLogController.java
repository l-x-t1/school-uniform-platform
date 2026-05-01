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
import com.xfgl.supplier.domain.XfptGoodsPublishLog;
import com.xfgl.supplier.service.IXfptGoodsPublishLogService;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;

/**
 * 商品上下架日志Controller
 *
 * @author yinet
 * @date 2026-05-01
 */
@Tag(name="商品上下架日志管理")
@RestController
@RequestMapping("/goodsPublishLog")
public class XfptGoodsPublishLogController extends BaseController {
    @Autowired
    private IXfptGoodsPublishLogService xfptGoodsPublishLogService;

/**
 * 查询商品上下架日志列表
 */

@Operation(summary = "查询商品上下架日志列表")
@RequiresPermissions("supplier:goodsPublishLog:list")
@GetMapping("/list")
    public TableDataInfo list(XfptGoodsPublishLog xfptGoodsPublishLog) {
        startPage();
        List<XfptGoodsPublishLog> list = xfptGoodsPublishLogService.selectXfptGoodsPublishLogList(xfptGoodsPublishLog);
        return getDataTable(list);
    }

    /**
     * 导出商品上下架日志列表
     */
    @Operation(summary = "导出商品上下架日志列表")
    @RequiresPermissions("supplier:goodsPublishLog:export")
    @Log(title = "商品上下架日志" , businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptGoodsPublishLog xfptGoodsPublishLog) {
        List<XfptGoodsPublishLog> list = xfptGoodsPublishLogService.selectXfptGoodsPublishLogList(xfptGoodsPublishLog);
        ExcelUtil<XfptGoodsPublishLog> util = new ExcelUtil<XfptGoodsPublishLog>(XfptGoodsPublishLog. class);
        util.exportExcel(response, list, "商品上下架日志数据");
    }

    /**
     * 获取商品上下架日志详细信息
     */
    @Operation(summary = "获取商品上下架日志详细信息")
    @RequiresPermissions("supplier:goodsPublishLog:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
            return success(xfptGoodsPublishLogService.getById(id));
    }

    /**
     * 新增商品上下架日志
     */
    @Operation(summary = "新增商品上下架日志")
    @RequiresPermissions("supplier:goodsPublishLog:add")
    @Log(title = "商品上下架日志" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptGoodsPublishLog xfptGoodsPublishLog) {
            return toAjax(xfptGoodsPublishLogService.save(xfptGoodsPublishLog));
    }

    /**
     * 修改商品上下架日志
     */
    @Operation(summary = "修改商品上下架日志")
    @RequiresPermissions("supplier:goodsPublishLog:edit")
    @Log(title = "商品上下架日志" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptGoodsPublishLog xfptGoodsPublishLog) {
            return toAjax(xfptGoodsPublishLogService.updateById(xfptGoodsPublishLog));
    }


    /**
     * 删除商品上下架日志
     */
    @Operation(summary = "删除商品上下架日志")
    @RequiresPermissions("supplier:goodsPublishLog:remove")
    @Log(title = "商品上下架日志" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
            return toAjax(xfptGoodsPublishLogService.removeByIds(Arrays.asList(ids)));
    }
}
