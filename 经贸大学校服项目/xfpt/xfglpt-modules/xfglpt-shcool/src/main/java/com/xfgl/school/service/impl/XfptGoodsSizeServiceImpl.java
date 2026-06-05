package com.xfgl.school.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.domain.XfptGoodsSize;
import com.xfgl.school.mapper.XfptGoodsSizeMapper;
import com.xfgl.school.service.IXfptGoodsSizeService;
import com.xfgl.school.support.SupplierAccessSupport;
import com.xfgl.system.api.domain.SysUser;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 商品尺码Service业务层处理
 *
 * @author yinet
 * @date 2026-05-04
 */
@Service
public class XfptGoodsSizeServiceImpl extends ServiceImpl<XfptGoodsSizeMapper, XfptGoodsSize> implements IXfptGoodsSizeService {

    @Autowired
    private XfptGoodsSizeMapper xfptGoodsSizeMapper;

    @Autowired
    private SupplierAccessSupport supplierAccessSupport;

    /**
     * 查询商品尺码列表
     *
     * @param xfptGoodsSize 商品尺码
     * @return 商品尺码
     */
    @Override
    public List<XfptGoodsSize> selectXfptGoodsSizeList(XfptGoodsSize xfptGoodsSize)
    {
        if (isSupplierScopedUser() && getCurrentSupplierIdIfScoped() == null) {
            return Collections.emptyList();
        }
        applyCurrentSupplierScope(xfptGoodsSize);
        return xfptGoodsSizeMapper.selectXfptGoodsSizeList(xfptGoodsSize);
    }

    private void applyCurrentSupplierScope(XfptGoodsSize xfptGoodsSize)
    {
        if (xfptGoodsSize == null) {
            return;
        }
        Long currentSupplierId = getCurrentSupplierIdIfScoped();
        if (currentSupplierId != null) {
            xfptGoodsSize.setSupplierId(currentSupplierId);
        }
    }

    private Long getCurrentSupplierIdIfScoped()
    {
        SysUser currentUser = getCurrentUser();
        if (!supplierAccessSupport.isSupplierUser(currentUser)) {
            return null;
        }
        return supplierAccessSupport.resolveSupplierId(currentUser);
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

}
