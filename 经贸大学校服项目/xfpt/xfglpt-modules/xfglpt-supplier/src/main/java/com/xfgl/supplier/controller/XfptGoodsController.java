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
import com.xfgl.supplier.domain.XfptGoods;
import com.xfgl.supplier.service.IXfptGoodsService;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;

/**
 * 商品信息Controller
 *
 * @author yinet
 * @date 2026-05-01
 */
@Tag(name="商品信息管理")
@RestController
@RequestMapping("/goods")
public class XfptGoodsController extends BaseController {
    @Autowired
    private IXfptGoodsService xfptGoodsService;

/**
 * 查询商品信息列表
 */

@Operation(summary = "查询商品信息列表")
@RequiresPermissions("supplier:goods:list")
@GetMapping("/list")
    public TableDataInfo list(XfptGoods xfptGoods) {
        startPage();
        List<XfptGoods> list = xfptGoodsService.selectXfptGoodsList(xfptGoods);
        return getDataTable(list);
    }

    /**
     * 导出商品信息列表
     */
    @Operation(summary = "导出商品信息列表")
    @RequiresPermissions("supplier:goods:export")
    @Log(title = "商品信息" , businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptGoods xfptGoods) {
        List<XfptGoods> list = xfptGoodsService.selectXfptGoodsList(xfptGoods);
        ExcelUtil<XfptGoods> util = new ExcelUtil<XfptGoods>(XfptGoods. class);
        util.exportExcel(response, list, "商品信息数据");
    }

    /**
     * 获取商品信息详细信息
     */
    @Operation(summary = "获取商品信息详细信息")
    @RequiresPermissions("supplier:goods:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
            return success(xfptGoodsService.getById(id));
    }

    /**
     * 新增商品信息
     */
    @Operation(summary = "新增商品信息")
    @RequiresPermissions("supplier:goods:add")
    @Log(title = "商品信息" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptGoods xfptGoods) {
            return toAjax(xfptGoodsService.save(xfptGoods));
    }

    /**
     * 修改商品信息
     */
    @Operation(summary = "修改商品信息")
    @RequiresPermissions("supplier:goods:edit")
    @Log(title = "商品信息" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptGoods xfptGoods) {
            return toAjax(xfptGoodsService.updateById(xfptGoods));
    }


    /**
     * 删除商品信息
     */
    @Operation(summary = "删除商品信息")
    @RequiresPermissions("supplier:goods:remove")
    @Log(title = "商品信息" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
            return toAjax(xfptGoodsService.removeByIds(Arrays.asList(ids)));
    }
}
