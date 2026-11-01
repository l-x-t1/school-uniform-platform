package com.xfgl.school.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.domain.XfptAfterSale;
import com.xfgl.school.domain.XfptSchoolMessage;
import com.xfgl.school.mapper.XfptAfterSaleMapper;
import com.xfgl.school.service.IXfptAfterSaleService;
import com.xfgl.school.support.SchoolAccessSupport;
import com.xfgl.school.support.SupplierAccessSupport;
import com.xfgl.system.api.domain.SysUser;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 售后主Service业务层处理
 *
 * @author yinet
 * @date 2026-05-04
 */
@Service
public class XfptAfterSaleServiceImpl extends ServiceImpl<XfptAfterSaleMapper, XfptAfterSale> implements IXfptAfterSaleService {

    @Autowired
    private XfptAfterSaleMapper xfptAfterSaleMapper;

    @Autowired
    private SupplierAccessSupport supplierAccessSupport;
    @Autowired
    private SchoolAccessSupport schoolAccessSupport;

    /**
     * 查询售后主列表
     *
     * @param xfptAfterSale 售后主
     * @return 售后主
     */
    @Override
    public List<XfptAfterSale> selectXfptAfterSaleList(XfptAfterSale xfptAfterSale)
    {
        if (xfptAfterSale == null) {
            xfptAfterSale = new XfptAfterSale();
        }
        XfptSchoolMessage currentSchool = getCurrentSchoolIfScoped();
        if (isSchoolScopedUser() && currentSchool == null) {
            return Collections.emptyList();
        }
        if (currentSchool != null) {
            xfptAfterSale.setSchoolId(currentSchool.getId());
            xfptAfterSale.setDeptId(currentSchool.getDeptId());
        }
        if (isSupplierScopedUser() && getCurrentSupplierIdIfScoped() == null) {
            return Collections.emptyList();
        }
        applyCurrentSupplierScope(xfptAfterSale);
        List<XfptAfterSale> list = xfptAfterSaleMapper.selectXfptAfterSaleList(xfptAfterSale);
        list.forEach(this::normalizeRefundFieldsForDisplay);
        return list;
    }

    @Override
    public XfptAfterSale getById(Serializable id)
    {
        XfptAfterSale afterSale = super.getById(id);
        requireAccessibleAfterSale(afterSale);
        normalizeRefundFieldsForDisplay(afterSale);
        return afterSale;
    }

    @Override
    public boolean updateById(XfptAfterSale entity)
    {
        XfptAfterSale existing = requireAccessibleAfterSale(super.getById(entity.getId()));
        if (entity.getSupplierId() == null) {
            entity.setSupplierId(existing.getSupplierId());
        }
        return super.updateById(entity);
    }

    @Override
    public boolean removeByIds(Collection<?> list)
    {
        if (list != null) {
            for (Object idObj : list) {
                if (idObj == null) {
                    continue;
                }
                Long id = Long.valueOf(String.valueOf(idObj));
                requireAccessibleAfterSale(super.getById(id));
            }
        }
        return super.removeByIds(list);
    }

    private void applyCurrentSupplierScope(XfptAfterSale xfptAfterSale)
    {
        if (xfptAfterSale == null) {
            return;
        }
        Long currentSupplierId = getCurrentSupplierIdIfScoped();
        if (isSupplierScopedUser() && currentSupplierId == null) {
            throw new ServiceException("当前登录账号未关联厂商，无法操作售后单");
        }
        if (currentSupplierId != null) {
            xfptAfterSale.setSupplierId(currentSupplierId);
        }
    }

    private XfptAfterSale requireAccessibleAfterSale(XfptAfterSale afterSale)
    {
        if (afterSale == null || !"0".equals(afterSale.getDelFlag())) {
            throw new ServiceException("售后单不存在");
        }
        XfptSchoolMessage currentSchool = getCurrentSchoolIfScoped();
        if (isSchoolScopedUser()) {
            if (currentSchool == null) {
                throw new ServiceException("当前登录学校信息不存在，无法操作售后单");
            }
            if (!Objects.equals(currentSchool.getId(), afterSale.getSchoolId())
                    || !Objects.equals(currentSchool.getDeptId(), afterSale.getDeptId())) {
                throw new ServiceException("无权操作其他学校的售后单");
            }
        }
        Long currentSupplierId = getCurrentSupplierIdIfScoped();
        if (isSupplierScopedUser() && currentSupplierId == null) {
            throw new ServiceException("当前登录账号未关联厂商，无法操作售后单");
        }
        if (currentSupplierId != null && !currentSupplierId.equals(afterSale.getSupplierId())) {
            throw new ServiceException("无权操作其他厂商的售后单");
        }
        return afterSale;
    }

    private Long getCurrentSupplierIdIfScoped()
    {
        SysUser currentUser = getCurrentUser();
        if (!supplierAccessSupport.isSupplierUser(currentUser)) {
            return null;
        }
        return supplierAccessSupport.resolveSupplierId(currentUser);
    }

    private XfptSchoolMessage getCurrentSchoolIfScoped()
    {
        SysUser currentUser = getCurrentUser();
        if (!schoolAccessSupport.isSchoolUser(currentUser)) {
            return null;
        }
        return schoolAccessSupport.resolveCurrentSchool(currentUser);
    }

    private boolean isSchoolScopedUser()
    {
        return schoolAccessSupport.isSchoolUser(getCurrentUser()) && !SecurityUtils.isAdmin(SecurityUtils.getUserId());
    }

    private boolean isSupplierScopedUser()
    {
        return supplierAccessSupport.isSupplierUser(getCurrentUser()) && !SecurityUtils.isAdmin(SecurityUtils.getUserId());
    }

    private SysUser getCurrentUser()
    {
        if (SecurityUtils.getLoginUser() == null) {
            return null;
        }
        return SecurityUtils.getLoginUser().getSysUser();
    }

    private void normalizeRefundFieldsForDisplay(XfptAfterSale afterSale)
    {
        if (afterSale == null) {
            return;
        }
        if (!isRefundApplyType(afterSale.getApplyType())) {
            return;
        }
        BigDecimal applyAmount = afterSale.getApplyAmount();
        BigDecimal actualRefundAmount = afterSale.getActualRefundAmount();
        if (applyAmount != null && (actualRefundAmount == null || BigDecimal.ZERO.compareTo(actualRefundAmount) == 0)) {
            afterSale.setActualRefundAmount(applyAmount);
            actualRefundAmount = applyAmount;
        }
        String refundStatus = afterSale.getRefundStatus();
        boolean refundAlreadyCompleted = "4".equals(String.valueOf(afterSale.getStatus()))
                || afterSale.getRefundTime() != null
                || afterSale.getFinishTime() != null
                || (actualRefundAmount != null && BigDecimal.ZERO.compareTo(actualRefundAmount) < 0);
        if (refundAlreadyCompleted && ("0".equals(String.valueOf(refundStatus))
                || refundStatus == null
                || refundStatus.trim().isEmpty())) {
            afterSale.setRefundStatus("2");
        }
    }

    private boolean isRefundApplyType(String applyType)
    {
        return "1".equals(String.valueOf(applyType)) || "2".equals(String.valueOf(applyType));
    }
}
