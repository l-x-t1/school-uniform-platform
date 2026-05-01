package com.xfgl.system.controller;

import java.util.List;
import java.util.Arrays;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xfgl.common.core.domain.R;
import com.xfgl.common.security.annotation.InnerAuth;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.system.domain.vo.ReleaseSupplementVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.xfgl.common.log.annotation.Log;
import com.xfgl.common.log.enums.BusinessType;
import com.xfgl.common.security.annotation.RequiresPermissions;
import com.xfgl.system.api.domain.XfptNotice;
import com.xfgl.system.service.IXfptNoticeService;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;

/**
 * 首页公告Controller
 *
 * @author yinet
 * @date 2025-04-23
 */
@Tag(name = "首页公告管理")
@RestController
@RequestMapping("/xfptnotice")
public class XfptNoticeController extends BaseController {
    @Autowired
    private IXfptNoticeService xfptNoticeService;


    /**
     * 查询首页公告列表
     */

    @Operation(summary = "查询首页公告列表")
    @RequiresPermissions("system:xfptnotice:list")
    @GetMapping("/list")
    public TableDataInfo list(XfptNotice xfptNotice) {
        startPage();
        List<XfptNotice> list = xfptNoticeService.selectXfptNoticeList(xfptNotice);
        return getDataTable(list);
    }
    /**
     * 查询首页公告列表
     */

    @Operation(summary = "查询首页公告列表")
    @RequiresPermissions("system:xfptnotice:list")
    @GetMapping("/list2")
    public TableDataInfo list2(XfptNotice xfptNotice) {
        startPage();
        List<XfptNotice> list = xfptNoticeService.selectXfptNoticeList(xfptNotice);
        return getDataTable(list);
    }
    /**
     * 导出首页公告列表
     */
    @Operation(summary = "导出首页公告列表")
    @RequiresPermissions("system:xfptnotice:export")
    @Log(title = "首页公告", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptNotice xfptNotice) {
        List<XfptNotice> list = xfptNoticeService.selectXfptNoticeList(xfptNotice);
        ExcelUtil<XfptNotice> util = new ExcelUtil<XfptNotice>(XfptNotice.class);
        util.exportExcel(response, list, "首页公告数据");
    }

    /**
     * 获取首页公告详细信息
     */
    //@Operation(summary = "获取首页公告详细信息")
    //@RequiresPermissions("system:xfptnotice:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(xfptNoticeService.getById(id));
    }

    /**
     * 新增首页公告
     */
    @Operation(summary = "新增首页公告")
    @RequiresPermissions("system:xfptnotice:add")
    @Log(title = "首页公告", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(XfptNotice xfptNotice) {
        xfptNotice.setUserId(SecurityUtils.getUserId());
        xfptNotice.setReadCount(0l);
        return toAjax(xfptNoticeService.save(xfptNotice));
    }

    /**
     * 修改首页公告
     */
    @Operation(summary = "修改首页公告")
    @RequiresPermissions("system:xfptnotice:edit")
    @Log(title = "首页公告", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptNotice xfptNotice) {
        return toAjax(xfptNoticeService.updateById(xfptNotice));
    }


    /**
     * 删除首页公告
     */
    @Operation(summary = "删除首页公告")
    @RequiresPermissions("system:xfptnotice:remove")
    @Log(title = "首页公告", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(xfptNoticeService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 新增首页公告
     */
    @InnerAuth
    @PostMapping("/save")
    public R<Boolean> save(@RequestBody XfptNotice xfptNotice) {
        return R.ok(xfptNoticeService.save(xfptNotice));
    }

    /**
     * 新增首页公告
     */
    @InnerAuth
    @PostMapping("/updateStatus/{id}/{status}")
    public R<Boolean> updateStatus(@PathVariable("id") Long id, @PathVariable("status") String status) {
        return R.ok(xfptNoticeService.lambdaUpdate().eq(XfptNotice::getEnteId, id).set(XfptNotice::getStatus, status).update());
    }

    /**
     * 删除首页公告
     */
    @InnerAuth
    @PostMapping("/removeNotice/{id}")
    public R<Boolean> removeNotice(@PathVariable("id") Long id) {
        LambdaQueryWrapper<XfptNotice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(XfptNotice::getEnteId, id);
        return R.ok(xfptNoticeService.remove(queryWrapper));
    }

    @GetMapping("/appList")
    public TableDataInfo appList(XfptNotice xfptNotice) {
        startPage();
        List<XfptNotice> xfptNotices = xfptNoticeService.appList(xfptNotice);
        return getDataTable(xfptNotices);
    }

    /**
     * 发布补充公告
     * @param releaseSupplementVo
     * @return
     */
    @PostMapping("/releaseSupplement")
    public AjaxResult releaseSupplement(ReleaseSupplementVo releaseSupplementVo){
        return xfptNoticeService.releaseSupplement(releaseSupplementVo);
    }

    /**
     * 查询补充公告
     * @param id  主键id
     * @param type  0采购公告  1合同公告
     * @param catagory 选购公告/合同公告 CGGG  企业入围公告QYRWGG
     * @return
     */
    @GetMapping("/selectReleaseSupplement")
    public AjaxResult selectReleaseSupplement(String id,String type,String catagory){
        return xfptNoticeService.selectReleaseSupplement(id,type,catagory);
    }
}
