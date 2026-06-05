package com.xfgl.school.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.page.PageMethod;
import com.xfgl.school.domain.SupplierUserRel;
import com.xfgl.school.mapper.SupplierUserRelMapper;
import com.xfgl.school.service.ISupplierUserRelService;
import com.xfgl.system.api.domain.SysUser;
import com.xfgl.common.security.utils.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 供应商用户关联Service业务层处理
 *
 * @date 2026-05-31
 */
@Service
public class SupplierUserRelServiceImpl
        extends ServiceImpl<SupplierUserRelMapper, SupplierUserRel>
        implements ISupplierUserRelService {

    @Override
    public void createRel(Long supplierId, Long userId) {
        SupplierUserRel rel = new SupplierUserRel();
        rel.setSupplierId(supplierId);
        rel.setUserId(userId);
        rel.setCreateTime(new Date());
        rel.setCreateBy(SecurityUtils.getUsername());
        save(rel);
    }

    @Override
    public List<Long> findSupplierIdsByUserId(Long userId) {
        return baseMapper.findSupplierIdsByUserId(userId);
    }

    @Override
    public Long findFirstSupplierIdByUserId(Long userId) {
        if (userId == null) {
            return null;
        }
        Page<?> currentPage = PageMethod.getLocalPage();
        try {
            PageMethod.clearPage();
            List<Long> supplierIds = baseMapper.findSupplierIdsByUserId(userId);
            return supplierIds == null || supplierIds.isEmpty() ? null : supplierIds.get(0);
        } finally {
            if (currentPage != null) {
                PageMethod.setLocalPage(currentPage);
            }
        }
    }

    @Override
    public List<Long> findUserIdsBySupplierId(Long supplierId) {
        return baseMapper.findUserIdsBySupplierId(supplierId);
    }

    @Override
    public List<SysUser> getUserListBySupplierId(Long supplierId) {
        return baseMapper.findUsersBySupplierId(supplierId);
    }

    @Override
    public void unbindUser(Long supplierId, Long userId) {
        baseMapper.deleteBySupplierAndUser(supplierId, userId);
    }
}
