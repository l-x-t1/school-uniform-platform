package com.xfgl.school.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Arrays;
import java.util.Date;
import java.util.Calendar;
import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.common.core.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.xfgl.school.domain.XfptDeliveryOrder;
import com.xfgl.school.domain.XfptDeliveryOrderItem;
import com.xfgl.school.domain.XfptProductionOrderItem;
import com.xfgl.school.mapper.XfptDeliveryOrderMapper;
import com.xfgl.school.mapper.XfptProductionOrderMapper;
import com.xfgl.school.domain.XfptProductionOrder;
import com.xfgl.school.service.IXfptProductionOrderService;

@Service
public class XfptProductionOrderServiceImpl extends ServiceImpl<XfptProductionOrderMapper, XfptProductionOrder> implements IXfptProductionOrderService {

    @Autowired
    private XfptProductionOrderMapper xfptProductionOrderMapper;

    @Autowired
    private XfptDeliveryOrderMapper xfptDeliveryOrderMapper;

    @Override
    public List<XfptProductionOrder> selectXfptProductionOrderList(XfptProductionOrder xfptProductionOrder)
    {
        LambdaQueryWrapper<XfptProductionOrder> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(StringUtils.isNotEmpty(xfptProductionOrder.getProductionNo()), XfptProductionOrder::getProductionNo, xfptProductionOrder.getProductionNo());
        queryWrapper.eq(xfptProductionOrder.getNoticeId() != null, XfptProductionOrder::getNoticeId, xfptProductionOrder.getNoticeId());
        queryWrapper.eq(xfptProductionOrder.getOrderId() != null, XfptProductionOrder::getOrderId, xfptProductionOrder.getOrderId());
        queryWrapper.like(StringUtils.isNotEmpty(xfptProductionOrder.getOrderNo()), XfptProductionOrder::getOrderNo, xfptProductionOrder.getOrderNo());
        queryWrapper.eq(StringUtils.isNotEmpty(xfptProductionOrder.getOrderType()), XfptProductionOrder::getOrderType, xfptProductionOrder.getOrderType());
        queryWrapper.eq(xfptProductionOrder.getSchoolId() != null, XfptProductionOrder::getSchoolId, xfptProductionOrder.getSchoolId());
        queryWrapper.eq(xfptProductionOrder.getSupplierId() != null, XfptProductionOrder::getSupplierId, xfptProductionOrder.getSupplierId());
        queryWrapper.eq(StringUtils.isNotEmpty(xfptProductionOrder.getStatus()), XfptProductionOrder::getStatus, xfptProductionOrder.getStatus());
        applyDayRange(queryWrapper, xfptProductionOrder.getPlannedFinishTime(), XfptProductionOrder::getPlannedFinishTime);
        applyDayRange(queryWrapper, xfptProductionOrder.getStartTime(), XfptProductionOrder::getStartTime);
        applyDayRange(queryWrapper, xfptProductionOrder.getFinishTime(), XfptProductionOrder::getFinishTime);
        queryWrapper.eq(xfptProductionOrder.getDeptId() != null, XfptProductionOrder::getDeptId, xfptProductionOrder.getDeptId());
        queryWrapper.eq(XfptProductionOrder::getDelFlag, "0");
        return this.list(queryWrapper);
    }

    @Override
    public XfptProductionOrder selectXfptProductionOrderById(Long id)
    {
        XfptProductionOrder xfptProductionOrder = xfptProductionOrderMapper.selectById(id);
        List<XfptProductionOrderItem> xfptProductionOrderItemList = xfptProductionOrderMapper.selectXfptProductionOrderItemById(id);
        xfptProductionOrder.setXfptProductionOrderItemList(xfptProductionOrderItemList);
        return xfptProductionOrder;
    }

    @Transactional
    @Override
    public int insertXfptProductionOrder(XfptProductionOrder xfptProductionOrder)
    {
        if (xfptProductionOrder.getStartTime() != null
                && xfptProductionOrder.getFinishTime() != null
                && !xfptProductionOrder.getStartTime().before(xfptProductionOrder.getFinishTime())) {
            throw new ServiceException("开始生产时间必须早于生产完成时间");
        }
        if (StringUtils.isEmpty(xfptProductionOrder.getOrderType())) {
            xfptProductionOrder.setOrderType("2");
        }
        if (StringUtils.isEmpty(xfptProductionOrder.getStatus())) {
            xfptProductionOrder.setStatus("0");
        }
        int rows = xfptProductionOrderMapper.insert(xfptProductionOrder);
        insertXfptProductionOrderItem(xfptProductionOrder);
        return rows;
    }

    @Transactional
    @Override
    public int updateXfptProductionOrder(XfptProductionOrder xfptProductionOrder)
    {
        xfptProductionOrderMapper.deleteXfptProductionOrderItemByProductionId(xfptProductionOrder.getId());
        insertXfptProductionOrderItem(xfptProductionOrder);
        return xfptProductionOrderMapper.updateById(xfptProductionOrder);
    }

    @Transactional
    @Override
    public int finishProductionOrder(Long id)
    {
        XfptProductionOrder productionOrder = selectXfptProductionOrderById(id);
        if (productionOrder == null) {
            throw new ServiceException("生产单不存在");
        }
        if ("2".equals(productionOrder.getStatus())) {
            return 1;
        }

        Date now = new Date();
        productionOrder.setStatus("2");
        productionOrder.setFinishTime(now);

        List<XfptProductionOrderItem> itemList = productionOrder.getXfptProductionOrderItemList();
        if (itemList == null || itemList.isEmpty()) {
            throw new ServiceException("生产单明细不能为空");
        }
        for (XfptProductionOrderItem item : itemList) {
            item.setProductionId(id);
            item.setStatus("2");
            item.setFinishNum(item.getBuyNum() == null ? 0 : item.getBuyNum());
            item.setUpdateTime(now);
        }

        xfptProductionOrderMapper.deleteXfptProductionOrderItemByProductionId(id);
        xfptProductionOrderMapper.batchInsertXfptProductionOrderItem(itemList);
        return xfptProductionOrderMapper.updateById(productionOrder);
    }

    @Transactional
    @Override
    public Long generateDeliveryOrder(Long id)
    {
        XfptProductionOrder productionOrder = selectXfptProductionOrderById(id);
        if (productionOrder == null) {
            throw new ServiceException("生产单不存在");
        }
        if (!"2".equals(productionOrder.getStatus())) {
            throw new ServiceException("生产单未完成，不能生成发货单");
        }

        Long exists = xfptDeliveryOrderMapper.selectCount(Wrappers.<XfptDeliveryOrder>lambdaQuery()
                .eq(XfptDeliveryOrder::getProductionId, id)
                .eq(XfptDeliveryOrder::getDelFlag, "0"));
        if (exists != null && exists > 0) {
            throw new ServiceException("该生产单已生成发货单");
        }

        Date now = new Date();
        XfptDeliveryOrder deliveryOrder = new XfptDeliveryOrder();
        deliveryOrder.setDeliveryNo("DEL-" + System.currentTimeMillis() + "-" + id);
        deliveryOrder.setNoticeId(productionOrder.getNoticeId());
        deliveryOrder.setOrderId(productionOrder.getOrderId());
        deliveryOrder.setOrderNo(productionOrder.getOrderNo());
        deliveryOrder.setProductionId(productionOrder.getId());
        deliveryOrder.setSchoolId(productionOrder.getSchoolId());
        deliveryOrder.setSupplierId(productionOrder.getSupplierId());
        deliveryOrder.setDeliveryStatus("0");
        deliveryOrder.setDeptId(productionOrder.getDeptId());
        deliveryOrder.setCreateTime(now);
        deliveryOrder.setDelFlag("0");
        xfptDeliveryOrderMapper.insert(deliveryOrder);

        List<XfptProductionOrderItem> productionItemList = productionOrder.getXfptProductionOrderItemList();
        if (productionItemList == null || productionItemList.isEmpty()) {
            throw new ServiceException("生产单明细不能为空");
        }
        List<XfptDeliveryOrderItem> deliveryItemList = new java.util.ArrayList<>();
        for (XfptProductionOrderItem productionItem : productionItemList) {
            XfptDeliveryOrderItem deliveryItem = new XfptDeliveryOrderItem();
            deliveryItem.setDeliveryId(deliveryOrder.getId());
            deliveryItem.setOrderItemId(productionItem.getOrderItemId());
            deliveryItem.setGoodsId(productionItem.getGoodsId());
            deliveryItem.setSizeId(productionItem.getSizeId());
            deliveryItem.setGoodsName(productionItem.getGoodsName());
            deliveryItem.setSizeName(productionItem.getSizeName());
            deliveryItem.setDeliveryNum(productionItem.getFinishNum() == null ? productionItem.getBuyNum() : productionItem.getFinishNum());
            deliveryItem.setSignNum(0);
            deliveryItem.setStatus("0");
            deliveryItem.setCreateTime(now);
            deliveryItem.setDelFlag("0");
            deliveryItemList.add(deliveryItem);
        }
        xfptDeliveryOrderMapper.batchInsertXfptDeliveryOrderItem(deliveryItemList);
        return deliveryOrder.getId();
    }

    @Transactional
    @Override
    public int deleteXfptProductionOrderByIds(Long[] ids)
    {
        List<Long> idList = Arrays.asList(ids);
        for (Long id : idList) {
            xfptProductionOrderMapper.deleteXfptProductionOrderItemByProductionId(id);
        }
        return xfptProductionOrderMapper.deleteBatchIds(idList);
    }

    @Transactional
    @Override
    public int deleteXfptProductionOrderById(Long id)
    {
        xfptProductionOrderMapper.deleteXfptProductionOrderItemByProductionId(id);
        return xfptProductionOrderMapper.deleteById(id);
    }

    private void insertXfptProductionOrderItem(XfptProductionOrder xfptProductionOrder)
    {
        List<XfptProductionOrderItem> xfptProductionOrderItemList = xfptProductionOrder.getXfptProductionOrderItemList();
        Long id = xfptProductionOrder.getId();

        if (StringUtils.isNotNull(xfptProductionOrderItemList)) {
            if (xfptProductionOrderItemList.isEmpty()) {
                throw new ServiceException("生产单明细不能为空");
            }

            for (XfptProductionOrderItem xfptProductionOrderItem : xfptProductionOrderItemList) {
                if (xfptProductionOrderItem.getGoodsId() == null) {
                    throw new ServiceException("生产单明细商品不能为空");
                }

                xfptProductionOrderItem.setProductionId(id);

                if (StringUtils.isEmpty(xfptProductionOrderItem.getStatus())) {
                    xfptProductionOrderItem.setStatus("0");
                }
                if (xfptProductionOrderItem.getFinishNum() == null) {
                    xfptProductionOrderItem.setFinishNum(0);
                }
            }

            xfptProductionOrderMapper.batchInsertXfptProductionOrderItem(xfptProductionOrderItemList);
        }
    }

    private <T> void applyDayRange(LambdaQueryWrapper<XfptProductionOrder> queryWrapper, Date value, com.baomidou.mybatisplus.core.toolkit.support.SFunction<XfptProductionOrder, T> column)
    {
        if (value == null) {
            return;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(value);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date start = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date end = calendar.getTime();
        queryWrapper.ge(column, start).lt(column, end);
    }
}
