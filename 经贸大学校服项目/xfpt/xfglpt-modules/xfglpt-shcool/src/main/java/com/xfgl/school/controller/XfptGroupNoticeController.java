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
import com.xfgl.school.domain.XfptGroupNotice;
import com.xfgl.school.service.IXfptGroupNoticeService;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;

/**
 * 统购公告Controller
 *
 * @author yinet
 * @date 2026-05-04
 */
@Tag(name="统购公告管理")
@RestController
@RequestMapping("/groupNotice")
public class XfptGroupNoticeController extends BaseController {
    @Autowired
    private IXfptGroupNoticeService xfptGroupNoticeService;

/**
 * 查询统购公告列表
 */

@Operation(summary = "查询统购公告列表")
@RequiresPermissions("school:groupNotice:list")
@GetMapping("/list")
    public TableDataInfo list(XfptGroupNotice xfptGroupNotice) {
        startPage();
        List<XfptGroupNotice> list = xfptGroupNoticeService.selectXfptGroupNoticeList(xfptGroupNotice);
        return getDataTable(list);
    }

    /**
     * 导出统购公告列表
     */
    @Operation(summary = "导出统购公告列表")
    @RequiresPermissions("school:groupNotice:export")
    @Log(title = "统购公告" , businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptGroupNotice xfptGroupNotice) {
        List<XfptGroupNotice> list = xfptGroupNoticeService.selectXfptGroupNoticeList(xfptGroupNotice);
        ExcelUtil<XfptGroupNotice> util = new ExcelUtil<XfptGroupNotice>(XfptGroupNotice. class);
        util.exportExcel(response, list, "统购公告数据");
    }

    /**
     * 获取统购公告详细信息
     */
    @Operation(summary = "获取统购公告详细信息")
    @RequiresPermissions("school:groupNotice:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
            return success(xfptGroupNoticeService.selectXfptGroupNoticeById(id));
    }

    /**
     * 新增统购公告
     */
    @Operation(summary = "新增统购公告")
    @RequiresPermissions("school:groupNotice:add")
    @Log(title = "统购公告" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptGroupNotice xfptGroupNotice) {
            return toAjax(xfptGroupNoticeService.insertXfptGroupNotice(xfptGroupNotice));
    }

    /**
     * 修改统购公告
     */
    @Operation(summary = "修改统购公告")
    @RequiresPermissions("school:groupNotice:edit")
    @Log(title = "统购公告" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptGroupNotice xfptGroupNotice) {
            return toAjax(xfptGroupNoticeService.updateXfptGroupNotice(xfptGroupNotice));
    }


    /**
     * 删除统购公告
     */
    @Operation(summary = "删除统购公告")
    @RequiresPermissions("school:groupNotice:remove")
    @Log(title = "统购公告" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
            return toAjax(xfptGroupNoticeService.deleteXfptGroupNoticeByIds(ids));
    }
}
