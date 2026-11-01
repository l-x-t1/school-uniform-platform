package com.xfgl.school.controller;

import cn.hutool.core.util.StrUtil;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.web.page.TableDataInfo;
import com.xfgl.common.log.annotation.Log;
import com.xfgl.common.log.enums.BusinessType;
import com.xfgl.school.domain.XfptParentComplaint;
import com.xfgl.school.service.IXfptParentComplaintService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Tag(name = "Parent complaint")
@RestController
@RequestMapping("/parentComplaint")
public class XfptParentComplaintController extends BaseController {

    @Autowired
    private IXfptParentComplaintService xfptParentComplaintService;

    @Operation(summary = "Complaint list")
    @GetMapping("/list")
    public TableDataInfo list(XfptParentComplaint complaint) {
        startPage();
        List<XfptParentComplaint> list = xfptParentComplaintService.lambdaQuery()
                .eq(StrUtil.isNotEmpty(complaint.getUserId()), XfptParentComplaint::getUserId, complaint.getUserId())
                .eq(StrUtil.isNotEmpty(complaint.getDeptId()), XfptParentComplaint::getDeptId, complaint.getDeptId())
                .eq(StrUtil.isNotEmpty(complaint.getStatus()), XfptParentComplaint::getStatus, complaint.getStatus())
                .like(StrUtil.isNotEmpty(complaint.getOrderNo()), XfptParentComplaint::getOrderNo, complaint.getOrderNo())
                .like(StrUtil.isNotEmpty(complaint.getGoodsName()), XfptParentComplaint::getGoodsName, complaint.getGoodsName())
                .like(StrUtil.isNotEmpty(complaint.getSupplierName()), XfptParentComplaint::getSupplierName, complaint.getSupplierName())
                .like(StrUtil.isNotEmpty(complaint.getComplaintType()), XfptParentComplaint::getComplaintType, complaint.getComplaintType())
                .orderByDesc(XfptParentComplaint::getCreateTime)
                .list();
        return getDataTable(list);
    }

    @Operation(summary = "My complaint list")
    @GetMapping("/list2")
    public TableDataInfo list2(XfptParentComplaint complaint) {
        startPage();
        List<XfptParentComplaint> list = xfptParentComplaintService.lambdaQuery()
                .eq(StrUtil.isNotEmpty(complaint.getUserId()), XfptParentComplaint::getUserId, complaint.getUserId())
                .eq(StrUtil.isNotEmpty(complaint.getStatus()), XfptParentComplaint::getStatus, complaint.getStatus())
                .orderByDesc(XfptParentComplaint::getCreateTime)
                .list();
        return getDataTable(list);
    }

    @Operation(summary = "Complaint detail")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(xfptParentComplaintService.getById(id));
    }

    @Operation(summary = "Add complaint")
    @Log(title = "Parent complaint", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(XfptParentComplaint complaint) {
        if (StrUtil.isBlank(complaint.getStatus())) {
            complaint.setStatus("0");
        }
        if (StrUtil.isBlank(complaint.getDelFlag())) {
            complaint.setDelFlag("0");
        }
        return toAjax(xfptParentComplaintService.save(complaint));
    }

    @Operation(summary = "Update complaint")
    @Log(title = "Parent complaint", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptParentComplaint complaint) {
        if (complaint.getId() == null) {
            return error("id is required");
        }
        if (StrUtil.isNotBlank(complaint.getStatus()) && "1".equals(complaint.getStatus()) && complaint.getHandleTime() == null) {
            complaint.setHandleTime(new Date());
        }
        return toAjax(xfptParentComplaintService.updateById(complaint));
    }

    @Operation(summary = "Delete complaint")
    @Log(title = "Parent complaint", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(xfptParentComplaintService.removeByIds(Arrays.asList(ids)));
    }
}
