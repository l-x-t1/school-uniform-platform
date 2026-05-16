package com.xfgl.school.controller;

import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xfgl.common.core.utils.StringUtils;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.web.page.TableDataInfo;
import com.xfgl.common.log.annotation.Log;
import com.xfgl.common.log.enums.BusinessType;
import com.xfgl.common.security.annotation.RequiresPermissions;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.domain.XfptWithdrawApply;
import com.xfgl.school.service.IXfptWithdrawApplyService;
import com.xfgl.system.api.domain.SysUser;
import com.xfgl.system.api.model.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 学校端申请结算列表Controller
 *
 * 复用供应商提现申请数据，首版只负责列表、详情和审核。
 */
@Tag(name = "学校端申请结算列表")
@RestController
@RequestMapping({"/settlementApply", "/school/settlementApply", "/xfglpt-school/settlementApply"})
public class XfptSettlementApplyController extends BaseController {
    @Autowired
    private IXfptWithdrawApplyService xfptWithdrawApplyService;

    /**
     * 查询申请结算列表
     */
    @Operation(summary = "查询申请结算列表")
    @RequiresPermissions("school:settlementApply:list")
    @GetMapping("/list")
    public TableDataInfo list(XfptWithdrawApply xfptWithdrawApply) {
        startPage();
        List<XfptWithdrawApply> list = xfptWithdrawApplyService.selectXfptWithdrawApplyList(xfptWithdrawApply);
        return getDataTable(list);
    }

    /**
     * 获取申请结算详细信息
     */
    @Operation(summary = "获取申请结算详细信息")
    @RequiresPermissions("school:settlementApply:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(xfptWithdrawApplyService.getById(id));
    }

    /**
     * 审核申请结算
     */
    @Operation(summary = "审核申请结算")
    @RequiresPermissions("school:settlementApply:audit")
    @Log(title = "申请结算审核", businessType = BusinessType.UPDATE)
    @PutMapping("/audit")
    public AjaxResult audit(@RequestBody XfptWithdrawApply auditApply) {
        if (auditApply == null || auditApply.getId() == null) {
            return error("申请结算记录ID不能为空");
        }
        if (!"1".equals(auditApply.getStatus()) && !"2".equals(auditApply.getStatus())) {
            return error("审核状态只能为通过或拒绝");
        }

        XfptWithdrawApply current = xfptWithdrawApplyService.getById(auditApply.getId());
        if (current == null || !"0".equals(current.getDelFlag())) {
            return error("申请结算记录不存在");
        }
        if (!"0".equals(current.getStatus())) {
            return error("只有待审核申请可以审核");
        }

        XfptWithdrawApply updateApply = new XfptWithdrawApply();
        updateApply.setId(auditApply.getId());
        updateApply.setStatus(auditApply.getStatus());
        updateApply.setAuditRemark(auditApply.getAuditRemark());
        updateApply.setAuditUserId(resolveAuditUserId());
        updateApply.setAuditUserName(resolveAuditUserName());
        updateApply.setAuditTime(new Date());
        updateApply.setUpdateBy(SecurityUtils.getUsername());
        updateApply.setUpdateTime(new Date());

        boolean updated = xfptWithdrawApplyService.update(
                updateApply,
                Wrappers.<XfptWithdrawApply>lambdaUpdate()
                        .eq(XfptWithdrawApply::getId, auditApply.getId())
                        .eq(XfptWithdrawApply::getStatus, "0")
                        .eq(XfptWithdrawApply::getDelFlag, "0")
        );
        if (!updated) {
            return error("只有待审核申请可以审核");
        }
        return success();
    }

    private Long resolveAuditUserId() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser != null && loginUser.getSysUser() != null && loginUser.getSysUser().getUserId() != null) {
            return loginUser.getSysUser().getUserId();
        }
        return SecurityUtils.getUserId();
    }

    private String resolveAuditUserName() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser != null && loginUser.getSysUser() != null) {
            SysUser sysUser = loginUser.getSysUser();
            if (StringUtils.isNotEmpty(sysUser.getNickName())) {
                return sysUser.getNickName();
            }
            if (StringUtils.isNotEmpty(sysUser.getUserName())) {
                return sysUser.getUserName();
            }
        }
        return SecurityUtils.getUsername();
    }
}
