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
import com.xfgl.school.domain.XfptGroupNoticeItem;
import com.xfgl.school.service.IXfptGroupNoticeItemService;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;

/**
 * 统购公告商品Controller
 *
 * @author yinet
 * @date 2026-05-04
 */
@Tag(name="统购公告商品管理")
@RestController
@RequestMapping("/groupNoticeItem")
public class XfptGroupNoticeItemController extends BaseController {
    @Autowired
    private IXfptGroupNoticeItemService xfptGroupNoticeItemService;

/**
 * 查询统购公告商品列表
 */

@Operation(summary = "查询统购公告商品列表")
@RequiresPermissions("school:groupNoticeItem:list")
@GetMapping("/list")
    public TableDataInfo list(XfptGroupNoticeItem xfptGroupNoticeItem) {
        startPage();
        List<XfptGroupNoticeItem> list = xfptGroupNoticeItemService.selectXfptGroupNoticeItemList(xfptGroupNoticeItem);
        return getDataTable(list);
    }

    /**
     * 导出统购公告商品列表
     */
    @Operation(summary = "导出统购公告商品列表")
    @RequiresPermissions("school:groupNoticeItem:export")
    @Log(title = "统购公告商品" , businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptGroupNoticeItem xfptGroupNoticeItem) {
        List<XfptGroupNoticeItem> list = xfptGroupNoticeItemService.selectXfptGroupNoticeItemList(xfptGroupNoticeItem);
        ExcelUtil<XfptGroupNoticeItem> util = new ExcelUtil<XfptGroupNoticeItem>(XfptGroupNoticeItem. class);
        util.exportExcel(response, list, "统购公告商品数据");
    }

    /**
     * 获取统购公告商品详细信息
     */
    @Operation(summary = "获取统购公告商品详细信息")
    @RequiresPermissions("school:groupNoticeItem:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
            return success(xfptGroupNoticeItemService.selectXfptGroupNoticeItemById(id));
    }

    /**
     * 新增统购公告商品
     */
    @Operation(summary = "新增统购公告商品")
    @RequiresPermissions("school:groupNoticeItem:add")
    @Log(title = "统购公告商品" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptGroupNoticeItem xfptGroupNoticeItem) {
            return toAjax(xfptGroupNoticeItemService.save(xfptGroupNoticeItem));
    }

    /**
     * 修改统购公告商品
     */
    @Operation(summary = "修改统购公告商品")
    @RequiresPermissions("school:groupNoticeItem:edit")
    @Log(title = "统购公告商品" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptGroupNoticeItem xfptGroupNoticeItem) {
            return toAjax(xfptGroupNoticeItemService.updateById(xfptGroupNoticeItem));
    }


    /**
     * 删除统购公告商品
     */
    @Operation(summary = "删除统购公告商品")
    @RequiresPermissions("school:groupNoticeItem:remove")
    @Log(title = "统购公告商品" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
            return toAjax(xfptGroupNoticeItemService.removeByIds(Arrays.asList(ids)));
    }
}
