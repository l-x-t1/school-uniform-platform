package com.xfgl.school.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.common.core.utils.StringUtils;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.domain.XfptDeliveryOrder;
import com.xfgl.school.domain.XfptDeliveryOrderItem;
import com.xfgl.school.domain.vo.DeliveryOrderStatisticsQuery;
import com.xfgl.school.domain.vo.DeliveryOrderStatisticsVo;
import com.xfgl.school.domain.vo.SupplierOptionVo;
import com.xfgl.school.mapper.XfptDeliveryOrderMapper;
import com.xfgl.school.service.IXfptDeliveryOrderService;
import com.xfgl.school.support.SupplierAccessSupport;
import com.xfgl.system.api.domain.SysUser;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 发货单Service业务层处理
 *
 * @author yinet
 * @date 2026-05-04
 */
@Service
public class XfptDeliveryOrderServiceImpl extends ServiceImpl<XfptDeliveryOrderMapper, XfptDeliveryOrder> implements IXfptDeliveryOrderService {

    @Autowired
    private XfptDeliveryOrderMapper xfptDeliveryOrderMapper;

    @Autowired
    private SupplierAccessSupport supplierAccessSupport;

    @Override
    public List<XfptDeliveryOrder> selectXfptDeliveryOrderList(XfptDeliveryOrder xfptDeliveryOrder) {
        if (isSupplierScopedUser() && getCurrentSupplierIdIfScoped() == null) {
            return Collections.emptyList();
        }
        applyCurrentSupplierScope(xfptDeliveryOrder);
        return xfptDeliveryOrderMapper.selectXfptDeliveryOrderList(xfptDeliveryOrder);
    }

    @Override
    public XfptDeliveryOrder selectXfptDeliveryOrderById(Long id) {
        XfptDeliveryOrder xfptDeliveryOrder = requireAccessibleDeliveryOrder(id);
        List<XfptDeliveryOrderItem> xfptDeliveryOrderItemList = xfptDeliveryOrderMapper.selectXfptDeliveryOrderItemById(id);
        xfptDeliveryOrder.setXfptDeliveryOrderItemList(xfptDeliveryOrderItemList);
        return xfptDeliveryOrder;
    }

    @Transactional
    @Override
    public int insertXfptDeliveryOrder(XfptDeliveryOrder xfptDeliveryOrder) {
        applyCurrentSupplierScope(xfptDeliveryOrder);
        if (xfptDeliveryOrder.getDeliveryTime() != null
                && xfptDeliveryOrder.getSignTime() != null
                && !xfptDeliveryOrder.getDeliveryTime().before(xfptDeliveryOrder.getSignTime())) {
            throw new ServiceException("发货时间必须早于签收时间");
        }
        fillDeliveryOrderDefaults(xfptDeliveryOrder);
        int rows = xfptDeliveryOrderMapper.insert(xfptDeliveryOrder);
        insertXfptDeliveryOrderItem(xfptDeliveryOrder);
        return rows;
    }

    @Transactional
    @Override
    public int updateXfptDeliveryOrder(XfptDeliveryOrder xfptDeliveryOrder) {
        requireAccessibleDeliveryOrder(xfptDeliveryOrder.getId());
        applyCurrentSupplierScope(xfptDeliveryOrder);
        fillDeliveryOrderDefaults(xfptDeliveryOrder);
        xfptDeliveryOrderMapper.deleteXfptDeliveryOrderItemByDeliveryId(xfptDeliveryOrder.getId());
        insertXfptDeliveryOrderItem(xfptDeliveryOrder);
        return xfptDeliveryOrderMapper.updateById(xfptDeliveryOrder);
    }

    @Transactional
    @Override
    public int deleteXfptDeliveryOrderByIds(Long[] ids) {
        List<Long> idList = Arrays.asList(ids);
        for (Long id : idList) {
            requireAccessibleDeliveryOrder(id);
            xfptDeliveryOrderMapper.deleteXfptDeliveryOrderItemByDeliveryId(id);
        }
        return xfptDeliveryOrderMapper.deleteBatchIds(idList);
    }

    @Transactional
    @Override
    public int deleteXfptDeliveryOrderById(Long id) {
        requireAccessibleDeliveryOrder(id);
        xfptDeliveryOrderMapper.deleteXfptDeliveryOrderItemByDeliveryId(id);
        return xfptDeliveryOrderMapper.deleteById(id);
    }

    @Override
    public List<DeliveryOrderStatisticsVo> selectDeliveryOrderStatisticsList(DeliveryOrderStatisticsQuery query) {
        return xfptDeliveryOrderMapper.selectDeliveryOrderStatisticsList(query);
    }

    @Override
    public List<SupplierOptionVo> selectDeliveryOrderSupplierOptions() {
        return xfptDeliveryOrderMapper.selectDeliveryOrderSupplierOptions(null, null);
    }

    private void insertXfptDeliveryOrderItem(XfptDeliveryOrder xfptDeliveryOrder) {
        List<XfptDeliveryOrderItem> xfptDeliveryOrderItemList = xfptDeliveryOrder.getXfptDeliveryOrderItemList();
        Long id = xfptDeliveryOrder.getId();

        if (StringUtils.isNotNull(xfptDeliveryOrderItemList)) {
            if (xfptDeliveryOrderItemList.isEmpty()) {
                throw new ServiceException("发货单明细不能为空");
            }

            for (XfptDeliveryOrderItem xfptDeliveryOrderItem : xfptDeliveryOrderItemList) {
                if (xfptDeliveryOrderItem.getGoodsId() == null) {
                    throw new ServiceException("发货单明细商品不能为空");
                }

                xfptDeliveryOrderItem.setDeliveryId(id);

                if (StringUtils.isEmpty(xfptDeliveryOrderItem.getStatus())) {
                    xfptDeliveryOrderItem.setStatus("0");
                }
                if (xfptDeliveryOrderItem.getSignNum() == null) {
                    xfptDeliveryOrderItem.setSignNum(0);
                }
                if (StringUtils.isEmpty(xfptDeliveryOrderItem.getDelFlag())) {
                    xfptDeliveryOrderItem.setDelFlag("0");
                }
            }

            xfptDeliveryOrderMapper.batchInsertXfptDeliveryOrderItem(xfptDeliveryOrderItemList);
        }
    }

    private void fillDeliveryOrderDefaults(XfptDeliveryOrder xfptDeliveryOrder) {
        if (StringUtils.isEmpty(xfptDeliveryOrder.getDeliveryStatus())) {
            xfptDeliveryOrder.setDeliveryStatus("0");
        }
        if (StringUtils.isEmpty(xfptDeliveryOrder.getDelFlag())) {
            xfptDeliveryOrder.setDelFlag("0");
        }
    }

    private void applyCurrentSupplierScope(XfptDeliveryOrder xfptDeliveryOrder) {
        if (xfptDeliveryOrder == null) {
            return;
        }
        Long currentSupplierId = getCurrentSupplierIdIfScoped();
        if (isSupplierScopedUser() && currentSupplierId == null) {
            throw new ServiceException("当前登录账号未关联厂商，无法操作发货单");
        }
        if (currentSupplierId != null) {
            xfptDeliveryOrder.setSupplierId(currentSupplierId);
        }
    }

    private XfptDeliveryOrder requireAccessibleDeliveryOrder(Long id) {
        if (id == null) {
            throw new ServiceException("发货单不存在");
        }
        Long currentSupplierId = getCurrentSupplierIdIfScoped();
        if (isSupplierScopedUser() && currentSupplierId == null) {
            throw new ServiceException("当前登录账号未关联厂商，无法操作发货单");
        }
        XfptDeliveryOrder deliveryOrder = xfptDeliveryOrderMapper.selectById(id);
        if (deliveryOrder == null || !"0".equals(deliveryOrder.getDelFlag())) {
            throw new ServiceException("发货单不存在");
        }
        if (currentSupplierId != null && !currentSupplierId.equals(deliveryOrder.getSupplierId())) {
            throw new ServiceException("无权操作其他厂商的发货单");
        }
        return deliveryOrder;
    }

    private Long getCurrentSupplierIdIfScoped() {
        SysUser currentUser = getCurrentUser();
        if (!supplierAccessSupport.isSupplierUser(currentUser)) {
            return null;
        }
        return supplierAccessSupport.resolveSupplierId(currentUser);
    }

    private boolean isSupplierScopedUser() {
        return supplierAccessSupport.isSupplierUser(getCurrentUser()) && !SecurityUtils.isAdmin(SecurityUtils.getUserId());
    }

    private SysUser getCurrentUser() {
        if (SecurityUtils.getLoginUser() == null) {
            return null;
        }
        return SecurityUtils.getLoginUser().getSysUser();
    }
}
