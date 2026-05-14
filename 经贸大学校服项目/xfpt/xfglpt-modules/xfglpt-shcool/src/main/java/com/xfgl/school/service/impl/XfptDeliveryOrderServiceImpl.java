package com.xfgl.school.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.common.core.utils.StringUtils;
import com.xfgl.school.domain.XfptDeliveryOrder;
import com.xfgl.school.domain.XfptDeliveryOrderItem;
import com.xfgl.school.mapper.XfptDeliveryOrderMapper;
import com.xfgl.school.service.IXfptDeliveryOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * 发货单Service业务层处理
 */
@Service
public class XfptDeliveryOrderServiceImpl extends ServiceImpl<XfptDeliveryOrderMapper, XfptDeliveryOrder> implements IXfptDeliveryOrderService {

    @Autowired
    private XfptDeliveryOrderMapper xfptDeliveryOrderMapper;

    @Override
    public List<XfptDeliveryOrder> selectXfptDeliveryOrderList(XfptDeliveryOrder xfptDeliveryOrder) {
        return xfptDeliveryOrderMapper.selectXfptDeliveryOrderList(xfptDeliveryOrder);
    }

    @Override
    public XfptDeliveryOrder selectXfptDeliveryOrderById(Long id) {
        XfptDeliveryOrder xfptDeliveryOrder = xfptDeliveryOrderMapper.selectById(id);
        if (xfptDeliveryOrder == null) {
            return null;
        }
        List<XfptDeliveryOrderItem> xfptDeliveryOrderItemList = xfptDeliveryOrderMapper.selectXfptDeliveryOrderItemById(id);
        xfptDeliveryOrder.setXfptDeliveryOrderItemList(xfptDeliveryOrderItemList);
        return xfptDeliveryOrder;
    }

    @Transactional
    @Override
    public int insertXfptDeliveryOrder(XfptDeliveryOrder xfptDeliveryOrder) {
        if (xfptDeliveryOrder.getDeliveryTime() != null
                && xfptDeliveryOrder.getSignTime() != null
                && !xfptDeliveryOrder.getDeliveryTime().before(xfptDeliveryOrder.getSignTime())) {
            throw new ServiceException("发货时间必须早于签收时间");
        }
        if (StringUtils.isEmpty(xfptDeliveryOrder.getDeliveryStatus())) {
            xfptDeliveryOrder.setDeliveryStatus("0");
        }
        int rows = xfptDeliveryOrderMapper.insert(xfptDeliveryOrder);
        insertXfptDeliveryOrderItem(xfptDeliveryOrder);
        return rows;
    }

    @Transactional
    @Override
    public int updateXfptDeliveryOrder(XfptDeliveryOrder xfptDeliveryOrder) {
        xfptDeliveryOrderMapper.deleteXfptDeliveryOrderItemByDeliveryId(xfptDeliveryOrder.getId());
        insertXfptDeliveryOrderItem(xfptDeliveryOrder);
        return xfptDeliveryOrderMapper.updateById(xfptDeliveryOrder);
    }

    @Transactional
    @Override
    public int deleteXfptDeliveryOrderByIds(Long[] ids) {
        List<Long> idList = Arrays.asList(ids);
        for (Long id : idList) {
            xfptDeliveryOrderMapper.deleteXfptDeliveryOrderItemByDeliveryId(id);
        }
        return xfptDeliveryOrderMapper.deleteBatchIds(idList);
    }

    @Transactional
    @Override
    public int deleteXfptDeliveryOrderById(Long id) {
        xfptDeliveryOrderMapper.deleteXfptDeliveryOrderItemByDeliveryId(id);
        return xfptDeliveryOrderMapper.deleteById(id);
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
}
