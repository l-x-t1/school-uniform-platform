package com.xfgl.school.controller;

import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.web.page.TableDataInfo;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.domain.XfptSupplierApply;
import com.xfgl.school.domain.vo.SupplierApplyNoticeVo;
import com.xfgl.school.domain.vo.SupplierApplySubmitRequest;
import com.xfgl.school.service.IXfptSupplierApplyService;
import com.xfgl.school.support.SupplierAccessSupport;
import com.xfgl.system.api.domain.SysUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "供应商报名门户")
@RestController
@RequestMapping("/supplierApplyPortal")
public class SupplierApplyPortalController extends BaseController {
    @Autowired
    private IXfptSupplierApplyService xfptSupplierApplyService;

    @Autowired
    private SupplierAccessSupport supplierAccessSupport;

    @Operation(summary = "报名中心公告列表")
    @GetMapping("/notice/list")
    public TableDataInfo noticeList(XfptSupplierApply query) {
        Long supplierId = resolveSupplierId();
        startPage();
        List<SupplierApplyNoticeVo> list = xfptSupplierApplyService.selectSupplierNoticeCenterList(supplierId, query);
        return getDataTable(list);
    }

    @Operation(summary = "报名详情上下文")
    @GetMapping("/context/{noticeId}")
    public AjaxResult context(@PathVariable("noticeId") Long noticeId) {
        return success(xfptSupplierApplyService.buildSupplierApplyContext(resolveSupplierId(), noticeId));
    }

    @Operation(summary = "保存报名草稿")
    @PostMapping("/saveDraft")
    public AjaxResult saveDraft(@Validated @RequestBody SupplierApplySubmitRequest request) {
        return success(xfptSupplierApplyService.saveSupplierDraft(resolveSupplierId(), request));
    }

    @Operation(summary = "提交报名")
    @PostMapping("/submit")
    public AjaxResult submit(@Validated @RequestBody SupplierApplySubmitRequest request) {
        return success(xfptSupplierApplyService.submitSupplierApply(resolveSupplierId(), request));
    }

    @Operation(summary = "撤回报名")
    @PostMapping("/withdraw/{noticeId}")
    public AjaxResult withdraw(@PathVariable("noticeId") Long noticeId) {
        xfptSupplierApplyService.withdrawSupplierApply(resolveSupplierId(), noticeId);
        return success();
    }

    @Operation(summary = "我的报名列表")
    @GetMapping("/my/list")
    public TableDataInfo myApplyList(XfptSupplierApply query) {
        Long supplierId = resolveSupplierId();
        query.setSupplierId(supplierId);
        startPage();
        List<XfptSupplierApply> list = xfptSupplierApplyService.selectXfptSupplierApplyList(query);
        return getDataTable(list);
    }

    @Operation(summary = "我的报名详情")
    @GetMapping("/my/{id}")
    public AjaxResult myApplyDetail(@PathVariable("id") Long id) {
        Long supplierId = resolveSupplierId();
        XfptSupplierApply apply = xfptSupplierApplyService.selectXfptSupplierApplyById(id);
        if (apply == null || apply.getSupplierId() == null || !apply.getSupplierId().equals(supplierId)) {
            throw new ServiceException("无权查看该报名记录");
        }
        Map<String, Object> context = xfptSupplierApplyService.buildSupplierApplyContext(supplierId, apply.getNoticeId());
        return success(context);
    }

    private Long resolveSupplierId() {
        SysUser sysUser = SecurityUtils.getLoginUser().getSysUser();
        if (!supplierAccessSupport.isSupplierUser(sysUser)) {
            throw new ServiceException("当前账号不是供应商账号，无法执行报名操作");
        }
        Long supplierId = supplierAccessSupport.resolveSupplierId(sysUser);
        if (supplierId == null) {
            throw new ServiceException("当前账号尚未完成供应商入驻或未绑定供应商档案");
        }
        return supplierId;
    }
}
