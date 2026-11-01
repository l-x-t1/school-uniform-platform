package com.xfgl.school.controller;
import java.util.List;
import java.util.Arrays;
import java.util.Map;
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
import com.xfgl.school.domain.XfptGoods;
import com.xfgl.school.domain.XfptGoodsSize;
import com.xfgl.school.domain.XfptGroupNotice;
import com.xfgl.school.domain.XfptSchoolSupplierRel;
import com.xfgl.school.domain.vo.GroupNoticeProgressQuery;
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
    public TableDataInfo list(XfptGroupNotice xfptGroupNotice, String simId) {
        if (simId != null && !"".equals(simId.trim())) {
            xfptGroupNotice.getParams().put("simId", simId.trim());
        }
        startPage();
        List<XfptGroupNotice> list = xfptGroupNoticeService.selectXfptGroupNoticeList(xfptGroupNotice);
        return getDataTable(list);
    }

@Operation(summary = "供应商查询学校发布的统购公告列表")
@RequiresPermissions("supplier:groupNotice:list")
@GetMapping("/supplier/list")
    public TableDataInfo supplierList(XfptGroupNotice xfptGroupNotice) {
        startPage();
        List<XfptGroupNotice> list = xfptGroupNoticeService.selectSupplierGroupNoticeList(xfptGroupNotice);
        return getDataTable(list);
    }

@Operation(summary = "供应商查询合作学校发布的选购公告列表")
@RequiresPermissions("supplier:groupNotice:list")
@GetMapping("/supplier/cooperation/list")
    public TableDataInfo supplierCooperationList(XfptGroupNotice xfptGroupNotice) {
        startPage();
        List<XfptGroupNotice> list = xfptGroupNoticeService.selectSupplierCooperationGroupNoticeList(xfptGroupNotice);
        return getDataTable(list);
    }

@Operation(summary = "查询统购订购进度列表")
@RequiresPermissions("school:tgjd:list")
@GetMapping("/progress/list")
    public TableDataInfo progressList(GroupNoticeProgressQuery query) {
        startPage();
        return getDataTable(xfptGroupNoticeService.selectGroupNoticeProgressList(query));
    }

@Operation(summary = "查询统购订购进度详情")
@RequiresPermissions("school:tgjd:query")
@GetMapping("/progress/{orderId}")
    public AjaxResult progressDetail(@PathVariable("orderId") Long orderId, Long supplierId) {
        return success(xfptGroupNoticeService.selectGroupNoticeOrderDetail(orderId, supplierId));
    }

@Operation(summary = "供应商查看统购订单")
@RequiresPermissions("school:tgjd:query")
@GetMapping("/progress/order/{orderId}")
    public AjaxResult viewProgressOrder(@PathVariable("orderId") Long orderId) {
        return success(xfptGroupNoticeService.viewGroupNoticeOrder(orderId));
    }

@Operation(summary = "供应商开始制作统购订单")
@RequiresPermissions("school:tgjd:edit")
@PutMapping("/progress/order/{orderId}/start")
    public AjaxResult startProgressOrder(@PathVariable("orderId") Long orderId) {
        return toAjax(xfptGroupNoticeService.startGroupNoticeOrder(orderId));
    }

@Operation(summary = "供应商完成制作统购订单")
@RequiresPermissions("school:tgjd:edit")
@PutMapping("/progress/order/{orderId}/finish")
    public AjaxResult finishProgressOrder(@PathVariable("orderId") Long orderId) {
        return toAjax(xfptGroupNoticeService.finishGroupNoticeOrder(orderId));
    }

@Operation(summary = "供应商填写统购订单物流单号")
@RequiresPermissions("school:tgjd:edit")
@PutMapping("/progress/order/{orderId}/logistics")
    public AjaxResult fillProgressOrderLogistics(@PathVariable("orderId") Long orderId, @RequestBody Map<String, String> body) {
        return toAjax(xfptGroupNoticeService.fillGroupNoticeOrderLogistics(orderId, body == null ? null : body.get("logisticsNo")));
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
    public AjaxResult getInfo(@PathVariable("id") Long id, String simId) {
            return success(xfptGroupNoticeService.selectXfptGroupNoticeById(id, simId));
    }

    @Operation(summary = "供应商获取学校发布的统购公告详细信息")
    @RequiresPermissions("supplier:groupNotice:query")
    @GetMapping(value = "/supplier/{id}")
    public AjaxResult getSupplierInfo(@PathVariable("id") Long id) {
            return success(xfptGroupNoticeService.selectSupplierGroupNoticeById(id));
    }

    @Operation(summary = "供应商获取合作学校发布的选购公告详细信息")
    @RequiresPermissions("supplier:groupNotice:query")
    @GetMapping(value = "/supplier/cooperation/{id}")
    public AjaxResult getSupplierCooperationInfo(@PathVariable("id") Long id) {
            return success(xfptGroupNoticeService.selectSupplierCooperationGroupNoticeById(id));
    }

    @Operation(summary = "查询统购公告可选合作供应商")
    @RequiresPermissions("school:groupNotice:query")
    @GetMapping("/supplierOptions")
    public AjaxResult supplierOptions(Long schoolId) {
        List<XfptSchoolSupplierRel> list = xfptGroupNoticeService.selectNoticeSupplierOptions(schoolId);
        return success(list);
    }

    @Operation(summary = "查询统购公告可选合作商品")
    @RequiresPermissions("school:groupNotice:query")
    @GetMapping("/goodsOptions")
    public AjaxResult goodsOptions(Long schoolId, String supplierIds) {
        List<XfptGoods> list = xfptGroupNoticeService.selectNoticeGoodsOptions(schoolId, supplierIds);
        return success(list);
    }

    @Operation(summary = "查询统购公告可选商品尺码")
    @RequiresPermissions("school:groupNotice:query")
    @GetMapping("/goodsSizeOptions")
    public TableDataInfo goodsSizeOptions(Long schoolId, String supplierIds) {
        List<XfptGoodsSize> list = xfptGroupNoticeService.selectNoticeGoodsSizeOptions(schoolId, supplierIds);
        return getDataTable(list);
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
