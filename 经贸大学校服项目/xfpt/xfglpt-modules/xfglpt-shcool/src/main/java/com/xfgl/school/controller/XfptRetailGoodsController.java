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
import com.xfgl.school.domain.vo.XfptRetailGoodsVo;
import com.xfgl.school.service.IXfptRetailGoodsService;
import com.xfgl.school.support.SupplierAccessSupport;
import com.xfgl.system.api.domain.SysRole;
import com.xfgl.system.api.domain.SysUser;
import com.xfgl.system.api.model.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 学校端零售商品Controller
 *
 * @author yinet
 */
@Tag(name = "学校端零售商品管理")
@RestController
@RequestMapping("/retailGoods")
public class XfptRetailGoodsController extends BaseController {
    @Autowired
    private IXfptRetailGoodsService xfptRetailGoodsService;

    @Autowired
    private SupplierAccessSupport supplierAccessSupport;

    /**
     * 查询学校端零售商品列表
     */
    @Operation(summary = "查询学校端零售商品列表")
    @RequiresPermissions("school:retailGoods:list")
    @GetMapping("/list")
    public TableDataInfo list(XfptRetailGoodsVo query) {
        RetailGoodsScope scope = getCurrentScope();
        query.setSchoolId(scope.getSchoolId());
        query.setSupplierId(scope.getSupplierId());
        startPage();
        List<XfptRetailGoodsVo> list = xfptRetailGoodsService.selectXfptRetailGoodsList(query);
        return getDataTable(list);
    }

    /**
     * 获取学校端零售商品详情
     */
    @Operation(summary = "获取学校端零售商品详情")
    @RequiresPermissions("school:retailGoods:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        RetailGoodsScope scope = getCurrentScope();
        return success(xfptRetailGoodsService.selectXfptRetailGoodsById(id, scope.getSchoolId(), scope.getSupplierId()));
    }

    /**
     * 修改学校端零售商品上下架状态
     */
    @Operation(summary = "修改学校端零售商品上下架状态")
    @RequiresPermissions("school:retailGoods:edit")
    @Log(title = "学校端零售商品", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}/status")
    public AjaxResult updateStatus(@PathVariable("id") Long id, @RequestBody XfptRetailGoodsVo retailGoodsVo) {
        if (retailGoodsVo == null || retailGoodsVo.getStatus() == null) {
            throw new ServiceException("上下架状态不能为空");
        }
        RetailGoodsScope scope = getCurrentScope();
        return toAjax(xfptRetailGoodsService.updateXfptRetailGoodsStatus(
                id, scope.getSchoolId(), scope.getSupplierId(), retailGoodsVo.getStatus(), SecurityUtils.getUsername()));
    }

    private RetailGoodsScope getCurrentScope() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser sysUser = loginUser == null ? null : loginUser.getSysUser();
        if (sysUser == null) {
            throw new ServiceException("当前登录用户信息不存在");
        }
        if (isGlobalUser(sysUser)) {
            return RetailGoodsScope.global();
        }
        if (supplierAccessSupport.isSupplierUser(sysUser)) {
            Long supplierId = supplierAccessSupport.resolveSupplierId(sysUser);
            if (supplierId == null) {
                throw new ServiceException("当前供应商账号未绑定供应商信息");
            }
            return RetailGoodsScope.supplier(supplierId);
        }
        if (sysUser.getDeptId() != null) {
            return RetailGoodsScope.school(sysUser.getDeptId());
        }
        throw new ServiceException("当前登录用户未绑定学校部门，且不具备供应商角色");
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

    private static class RetailGoodsScope {
        private final Long schoolId;
        private final Long supplierId;

        private RetailGoodsScope(Long schoolId, Long supplierId) {
            this.schoolId = schoolId;
            this.supplierId = supplierId;
        }

        static RetailGoodsScope global() {
            return new RetailGoodsScope(null, null);
        }

        static RetailGoodsScope school(Long schoolId) {
            return new RetailGoodsScope(schoolId, null);
        }

        static RetailGoodsScope supplier(Long supplierId) {
            return new RetailGoodsScope(null, supplierId);
        }

        Long getSchoolId() {
            return schoolId;
        }

        Long getSupplierId() {
            return supplierId;
        }
    }
}
