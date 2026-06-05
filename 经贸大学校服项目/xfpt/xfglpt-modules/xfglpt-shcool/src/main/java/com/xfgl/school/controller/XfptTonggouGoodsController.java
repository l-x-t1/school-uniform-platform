package com.xfgl.school.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.xfgl.common.core.enums.RoleType;
import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.common.core.utils.StringUtils;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.web.page.TableDataInfo;
import com.xfgl.common.log.annotation.Log;
import com.xfgl.common.log.enums.BusinessType;
import com.xfgl.common.security.annotation.RequiresPermissions;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.domain.XfptSchoolMessage;
import com.xfgl.school.domain.vo.XfptTonggouGoodsVo;
import com.xfgl.school.service.IXfptTonggouGoodsService;
import com.xfgl.school.support.SchoolAccessSupport;
import com.xfgl.school.support.SupplierAccessSupport;
import com.xfgl.system.api.domain.SysRole;
import com.xfgl.system.api.domain.SysUser;
import com.xfgl.system.api.model.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 学校端统购上下架 Controller
 *
 * @author Codex
 */
@Tag(name = "学校端统购上下架")
@RestController
@RequestMapping("/tonggouGoods")
public class XfptTonggouGoodsController extends BaseController {
    @Autowired
    private IXfptTonggouGoodsService xfptTonggouGoodsService;

    @Autowired
    private SupplierAccessSupport supplierAccessSupport;
    @Autowired
    private SchoolAccessSupport schoolAccessSupport;

    @Operation(summary = "查询学校端统购商品列表")
    @RequiresPermissions("school:tonggouGoods:list")
    @GetMapping("/list")
    public TableDataInfo list(XfptTonggouGoodsVo query) {
        TonggouGoodsScope scope = getCurrentScope();
        query.setSchoolId(scope.getSchoolId());
        query.setSupplierId(scope.getSupplierId());
        startPage();
        List<XfptTonggouGoodsVo> list = xfptTonggouGoodsService.selectXfptTonggouGoodsList(query);
        return getDataTable(list);
    }

    @Operation(summary = "获取学校端统购商品详情")
    @RequiresPermissions("school:tonggouGoods:query")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        TonggouGoodsScope scope = getCurrentScope();
        return success(xfptTonggouGoodsService.selectXfptTonggouGoodsById(id, scope.getSchoolId(), scope.getSupplierId()));
    }

    @Operation(summary = "修改学校端统购商品上下架状态")
    @RequiresPermissions("school:tonggouGoods:edit")
    @Log(title = "学校端统购商品", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}/status")
    public AjaxResult updateStatus(@PathVariable("id") Long id, @RequestBody XfptTonggouGoodsVo tonggouGoodsVo) {
        if (tonggouGoodsVo == null || tonggouGoodsVo.getStatus() == null) {
            throw new ServiceException("上下架状态不能为空");
        }
        TonggouGoodsScope scope = getCurrentScope();
        return toAjax(xfptTonggouGoodsService.updateXfptTonggouGoodsStatus(
                id, scope.getSchoolId(), scope.getSupplierId(), tonggouGoodsVo.getStatus(), SecurityUtils.getUsername()));
    }

    private TonggouGoodsScope getCurrentScope() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser sysUser = loginUser == null ? null : loginUser.getSysUser();
        if (sysUser == null) {
            throw new ServiceException("当前登录用户信息不存在");
        }
        if (isGlobalUser(sysUser)) {
            return TonggouGoodsScope.global();
        }
        if (supplierAccessSupport.isSupplierUser(sysUser)) {
            Long supplierId = supplierAccessSupport.resolveSupplierId(sysUser);
            if (supplierId != null) {
                return TonggouGoodsScope.supplier(supplierId);
            }
        }
        if (schoolAccessSupport.isSchoolUser(sysUser)) {
            XfptSchoolMessage currentSchool = schoolAccessSupport.resolveCurrentSchool(sysUser);
            if (currentSchool == null) {
                throw new ServiceException("当前登录学校信息不存在");
            }
            return TonggouGoodsScope.school(currentSchool.getId());
        }
        return TonggouGoodsScope.global();
    }

    private boolean isGlobalUser(SysUser sysUser) {
        if (sysUser == null) {
            return false;
        }
        if ("admin".equals(sysUser.getUserName())) {
            return true;
        }
        List<SysRole> roles = sysUser.getRoles();
        if (roles == null || roles.isEmpty()) {
            return false;
        }
        for (SysRole role : roles) {
            if (role == null) {
                continue;
            }
            String roleKey = role.getRoleKey();
            if (StringUtils.equalsAnyIgnoreCase(roleKey, RoleType.SJYJ.getCode(), RoleType.JYJ.getCode(), "admin")) {
                return true;
            }
        }
        return false;
    }

    private static class TonggouGoodsScope {
        private final Long schoolId;
        private final Long supplierId;

        private TonggouGoodsScope(Long schoolId, Long supplierId) {
            this.schoolId = schoolId;
            this.supplierId = supplierId;
        }

        static TonggouGoodsScope global() {
            return new TonggouGoodsScope(null, null);
        }

        static TonggouGoodsScope school(Long schoolId) {
            return new TonggouGoodsScope(schoolId, null);
        }

        static TonggouGoodsScope supplier(Long supplierId) {
            return new TonggouGoodsScope(null, supplierId);
        }

        Long getSchoolId() {
            return schoolId;
        }

        Long getSupplierId() {
            return supplierId;
        }
    }
}
