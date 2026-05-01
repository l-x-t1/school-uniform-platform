package com.xfgl.system.controller;

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
import com.xfgl.system.domain.XfptCarouselImage;
import com.xfgl.system.service.IXfptCarouselImageService;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;

/**
 * 轮播图Controller
 *
 * @author yinet
 * @date 2025-05-19
 */
@Tag(name = "轮播图管理")
@RestController
@RequestMapping("/lbimage")
public class XfptCarouselImageController extends BaseController {
    @Autowired
    private IXfptCarouselImageService xfptCarouselImageService;

    /**
     * 查询轮播图列表
     */

    @Operation(summary = "查询轮播图列表")
    @RequiresPermissions("system:lbimage:list")
    @GetMapping("/list")
    public TableDataInfo list(XfptCarouselImage xfptCarouselImage) {
        startPage();
        List<XfptCarouselImage> list = xfptCarouselImageService.selectXfptCarouselImageList(xfptCarouselImage);
        return getDataTable(list);
    }

    /**
     * 导出轮播图列表
     */
    @Operation(summary = "导出轮播图列表")
    @RequiresPermissions("system:lbimage:export")
    @Log(title = "轮播图", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptCarouselImage xfptCarouselImage) {
        List<XfptCarouselImage> list = xfptCarouselImageService.selectXfptCarouselImageList(xfptCarouselImage);
        ExcelUtil<XfptCarouselImage> util = new ExcelUtil<XfptCarouselImage>(XfptCarouselImage.class);
        util.exportExcel(response, list, "轮播图数据");
    }

    /**
     * 获取轮播图详细信息
     */
    @Operation(summary = "获取轮播图详细信息")
    @RequiresPermissions("system:lbimage:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(xfptCarouselImageService.getById(id));
    }

    /**
     * 新增轮播图
     */
    @Operation(summary = "新增轮播图")
    @RequiresPermissions("system:lbimage:add")
    @Log(title = "轮播图", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptCarouselImage xfptCarouselImage) {
        return toAjax(xfptCarouselImageService.save(xfptCarouselImage));
    }

    /**
     * 修改轮播图
     */
    @Operation(summary = "修改轮播图")
    @RequiresPermissions("system:lbimage:edit")
    @Log(title = "轮播图", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptCarouselImage xfptCarouselImage) {
        return toAjax(xfptCarouselImageService.updateById(xfptCarouselImage));
    }


    /**
     * 删除轮播图
     */
    @Operation(summary = "删除轮播图")
    @RequiresPermissions("system:lbimage:remove")
    @Log(title = "轮播图", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(xfptCarouselImageService.removeByIds(Arrays.asList(ids)));
    }
}
