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
import com.xfgl.school.domain.XfptGoodsSize;
import com.xfgl.school.service.IXfptGoodsSizeService;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;

/**
 * 商品尺码Controller
 *
 * @author yinet
 * @date 2026-05-04
 */
@Tag(name="商品尺码管理")
@RestController
@RequestMapping("/goodsSize")
public class XfptGoodsSizeController extends BaseController {
    @Autowired
    private IXfptGoodsSizeService xfptGoodsSizeService;

/**
 * 查询商品尺码列表
 */

@Operation(summary = "查询商品尺码列表")
@RequiresPermissions("school:goodsSize:list")
@GetMapping("/list")
    public TableDataInfo list(XfptGoodsSize xfptGoodsSize) {
        startPage();
        List<XfptGoodsSize> list = xfptGoodsSizeService.selectXfptGoodsSizeList(xfptGoodsSize);
        return getDataTable(list);
    }

    /**
     * 导出商品尺码列表
     */
    @Operation(summary = "导出商品尺码列表")
    @RequiresPermissions("school:goodsSize:export")
    @Log(title = "商品尺码" , businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptGoodsSize xfptGoodsSize) {
        List<XfptGoodsSize> list = xfptGoodsSizeService.selectXfptGoodsSizeList(xfptGoodsSize);
        ExcelUtil<XfptGoodsSize> util = new ExcelUtil<XfptGoodsSize>(XfptGoodsSize. class);
        util.exportExcel(response, list, "商品尺码数据");
    }

    /**
     * 获取商品尺码详细信息
     */
    @Operation(summary = "获取商品尺码详细信息")
    @RequiresPermissions("school:goodsSize:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
            return success(xfptGoodsSizeService.getById(id));
    }

    /**
     * 新增商品尺码
     */
    @Operation(summary = "新增商品尺码")
    @RequiresPermissions("school:goodsSize:add")
    @Log(title = "商品尺码" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptGoodsSize xfptGoodsSize) {
            return toAjax(xfptGoodsSizeService.save(xfptGoodsSize));
    }

    /**
     * 修改商品尺码
     */
    @Operation(summary = "修改商品尺码")
    @RequiresPermissions("school:goodsSize:edit")
    @Log(title = "商品尺码" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptGoodsSize xfptGoodsSize) {
            return toAjax(xfptGoodsSizeService.updateById(xfptGoodsSize));
    }


    /**
     * 删除商品尺码
     */
    @Operation(summary = "删除商品尺码")
    @RequiresPermissions("school:goodsSize:remove")
    @Log(title = "商品尺码" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
            return toAjax(xfptGoodsSizeService.removeByIds(Arrays.asList(ids)));
    }
}
