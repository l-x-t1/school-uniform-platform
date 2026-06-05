package com.xfgl.school.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.common.security.utils.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import com.xfgl.school.mapper.XfptOrderItemMapper;
import com.xfgl.school.mapper.XfptOrderInfoMapper;
import com.xfgl.school.domain.XfptOrderInfo;
import com.xfgl.school.domain.XfptSchoolMessage;
import com.xfgl.school.domain.XfptOrderItem;
import com.xfgl.school.service.IXfptOrderItemService;
import com.xfgl.school.support.SchoolAccessSupport;
import com.xfgl.system.api.domain.SysUser;

/**
 * 订单明细Service业务层处理
 *
 * @author yinet
 * @date 2026-05-04
 */
@Service
public class XfptOrderItemServiceImpl extends ServiceImpl<XfptOrderItemMapper, XfptOrderItem> implements IXfptOrderItemService {

    @Autowired
    private XfptOrderItemMapper xfptOrderItemMapper;
    @Autowired
    private XfptOrderInfoMapper xfptOrderInfoMapper;
    @Autowired
    private SchoolAccessSupport schoolAccessSupport;

    /**
     * 查询订单明细列表
     *
     * @param xfptOrderItem 订单明细
     * @return 订单明细
     */
    @Override
    public List<XfptOrderItem> selectXfptOrderItemList(XfptOrderItem xfptOrderItem)
    {
        if (xfptOrderItem == null) {
            xfptOrderItem = new XfptOrderItem();
        }
        XfptSchoolMessage currentSchool = getCurrentSchoolIfScoped();
        if (isSchoolScopedUser() && currentSchool == null) {
            return Collections.emptyList();
        }
        if (currentSchool != null) {
            xfptOrderItem.setDeptId(currentSchool.getDeptId());
        }
        return xfptOrderItemMapper.selectXfptOrderItemList(xfptOrderItem);
    }

    @Override
    public XfptOrderItem getById(Serializable id)
    {
        return requireAccessibleOrderItem(super.getById(id));
    }

    @Override
    public boolean save(XfptOrderItem xfptOrderItem)
    {
        applyCurrentSchoolScopeForWrite(xfptOrderItem, null);
        fillOrderItemDefaults(xfptOrderItem);
        return super.save(xfptOrderItem);
    }

    @Override
    public boolean updateById(XfptOrderItem xfptOrderItem)
    {
        XfptOrderItem existing = requireAccessibleOrderItem(super.getById(xfptOrderItem.getId()));
        applyCurrentSchoolScopeForWrite(xfptOrderItem, existing);
        fillOrderItemDefaults(xfptOrderItem);
        return super.updateById(xfptOrderItem);
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
                requireAccessibleOrderItem(super.getById(id));
            }
        }
        return super.removeByIds(list);
    }

    private void fillOrderItemDefaults(XfptOrderItem xfptOrderItem)
    {
        if (xfptOrderItem.getSaleType() == null || "".equals(xfptOrderItem.getSaleType())) {
            xfptOrderItem.setSaleType("1");
        }
        if (xfptOrderItem.getDelFlag() == null || "".equals(xfptOrderItem.getDelFlag())) {
            xfptOrderItem.setDelFlag("0");
        }
    }

    private void applyCurrentSchoolScopeForWrite(XfptOrderItem xfptOrderItem, XfptOrderItem existing)
    {
        if (!isSchoolScopedUser()) {
            return;
        }
        XfptSchoolMessage currentSchool = getCurrentSchoolIfScoped();
        if (currentSchool == null) {
            throw new ServiceException("当前登录学校信息不存在，无法保存订单明细");
        }
        Long orderId = xfptOrderItem.getOrderId();
        if (orderId == null && existing != null) {
            orderId = existing.getOrderId();
            xfptOrderItem.setOrderId(orderId);
        }
        if (orderId == null) {
            throw new ServiceException("订单不能为空");
        }
        XfptOrderInfo orderInfo = xfptOrderInfoMapper.selectById(orderId);
        if (orderInfo == null || !"0".equals(orderInfo.getDelFlag())) {
            throw new ServiceException("订单不存在");
        }
        if (!Objects.equals(orderInfo.getSchoolId(), currentSchool.getId())
                || !Objects.equals(orderInfo.getDeptId(), currentSchool.getDeptId())) {
            throw new ServiceException("只能操作当前学校的订单明细");
        }
        xfptOrderItem.setDeptId(currentSchool.getDeptId());
    }

    private XfptOrderItem requireAccessibleOrderItem(XfptOrderItem orderItem)
    {
        if (orderItem == null || !"0".equals(orderItem.getDelFlag())) {
            throw new ServiceException("订单明细不存在");
        }
        XfptSchoolMessage currentSchool = getCurrentSchoolIfScoped();
        if (isSchoolScopedUser()) {
            if (currentSchool == null) {
                throw new ServiceException("当前登录学校信息不存在，无法操作订单明细");
            }
            if (!Objects.equals(orderItem.getDeptId(), currentSchool.getDeptId())) {
                throw new ServiceException("只能操作当前学校的订单明细");
            }
        }
        return orderItem;
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

    private SysUser getCurrentUser()
    {
        return SecurityUtils.getLoginUser() == null ? null : SecurityUtils.getLoginUser().getSysUser();
    }

}
