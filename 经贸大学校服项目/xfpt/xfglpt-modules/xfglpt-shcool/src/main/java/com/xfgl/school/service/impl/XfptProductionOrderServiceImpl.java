package com.xfgl.school.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import com.xfgl.common.core.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.xfgl.school.domain.XfptOrderInfo;
import com.xfgl.school.domain.XfptOrderItem;
import com.xfgl.school.domain.XfptProductionOrderItem;
import com.xfgl.school.mapper.XfptProductionOrderMapper;
import com.xfgl.school.domain.XfptProductionOrder;
import com.xfgl.school.service.IXfptOrderInfoService;
import com.xfgl.school.service.IXfptOrderItemService;
import com.xfgl.school.service.IXfptProductionOrderItemService;
import com.xfgl.school.service.IXfptProductionOrderService;

/**
 * 生产单Service业务层处理
 *
 * @author yinet
 * @date 2026-05-04
 */
@Service
public class XfptProductionOrderServiceImpl extends ServiceImpl<XfptProductionOrderMapper, XfptProductionOrder> implements IXfptProductionOrderService {

    @Autowired
    private XfptProductionOrderMapper xfptProductionOrderMapper;

    @Autowired
    private IXfptOrderInfoService xfptOrderInfoService;

    @Autowired
    private IXfptOrderItemService xfptOrderItemService;

    @Autowired
    private IXfptProductionOrderItemService xfptProductionOrderItemService;

    /**
     * 查询生产单列表
     *
     * @param xfptProductionOrder 生产单
     * @return 生产单
     */
    @Override
    public List<XfptProductionOrder> selectXfptProductionOrderList(XfptProductionOrder xfptProductionOrder)
    {
        return xfptProductionOrderMapper.selectXfptProductionOrderList(xfptProductionOrder);
    }

        /**
         * 查询生产单
         *
         * @param id 生产单主键
         * @return 生产单
         */
        @Override
        public XfptProductionOrder selectXfptProductionOrderById(Long id)
        {
            XfptProductionOrder xfptProductionOrder = xfptProductionOrderMapper.selectById(id);
            List<XfptProductionOrderItem> xfptProductionOrderItemList = xfptProductionOrderMapper.selectXfptProductionOrderItemById(id);
            xfptProductionOrder.setXfptProductionOrderItemList(xfptProductionOrderItemList);
            return xfptProductionOrder;
        }

        /**
         * 新增生产单
         *
         * @param xfptProductionOrder 生产单
         * @return 结果
         */
        @Transactional
        @Override
        public int insertXfptProductionOrder(XfptProductionOrder xfptProductionOrder)
        {
            if (StringUtils.isEmpty(xfptProductionOrder.getOrderType())) {
                xfptProductionOrder.setOrderType("2");
            }
            if (StringUtils.isEmpty(xfptProductionOrder.getStatus())) {
                xfptProductionOrder.setStatus("0");
            }
            if (StringUtils.isEmpty(xfptProductionOrder.getDelFlag())) {
                xfptProductionOrder.setDelFlag("0");
            }
            int rows = xfptProductionOrderMapper.insert(xfptProductionOrder);
            insertXfptProductionOrderItem(xfptProductionOrder);
            return rows;
        }

        /**
         * 根据订单生成生产单
         *
         * @param orderId 订单ID
         * @return 生产单
         */
        @Transactional
        @Override
        public XfptProductionOrder generateFromOrder(Long orderId)
        {
            if (orderId == null) {
                throw new IllegalArgumentException("订单ID不能为空");
            }
            XfptOrderInfo orderInfo = xfptOrderInfoService.lambdaQuery()
                    .eq(XfptOrderInfo::getId, orderId)
                    .eq(XfptOrderInfo::getDelFlag, "0")
                    .one();
            if (orderInfo == null) {
                throw new IllegalArgumentException("订单不存在或已删除");
            }
            if (!"1".equals(orderInfo.getOrderStatus())) {
                throw new IllegalArgumentException("只有待生产订单才能生成生产单");
            }
            long count = lambdaQuery()
                    .eq(XfptProductionOrder::getOrderId, orderId)
                    .eq(XfptProductionOrder::getDelFlag, "0")
                    .count();
            if (count > 0) {
                throw new IllegalArgumentException("该订单已生成生产单，不能重复生成");
            }
            List<XfptOrderItem> orderItems = xfptOrderItemService.lambdaQuery()
                    .eq(XfptOrderItem::getOrderId, orderId)
                    .eq(XfptOrderItem::getDelFlag, "0")
                    .list();
            if (orderItems == null || orderItems.isEmpty()) {
                throw new IllegalArgumentException("订单没有明细，不能生成生产单");
            }

            XfptProductionOrder productionOrder = new XfptProductionOrder();
            productionOrder.setProductionNo(buildProductionNo(orderId));
            productionOrder.setNoticeId(orderInfo.getNoticeId());
            productionOrder.setOrderId(orderInfo.getId());
            productionOrder.setOrderNo(orderInfo.getOrderNo());
            productionOrder.setOrderType(orderInfo.getOrderType());
            productionOrder.setSchoolId(orderInfo.getSchoolId());
            productionOrder.setSupplierId(orderInfo.getSupplierId());
            productionOrder.setStatus("0");
            productionOrder.setDeptId(orderInfo.getDeptId());
            productionOrder.setDelFlag("0");
            productionOrder.setXfptProductionOrderItemList(buildProductionOrderItems(orderItems));
            insertXfptProductionOrder(productionOrder);
            return productionOrder;
        }

        /**
         * 开始生产
         *
         * @param id 生产单主键
         * @return 结果
         */
        @Transactional
        @Override
        public int startProduction(Long id)
        {
            XfptProductionOrder productionOrder = getUsableProductionOrder(id);
            if (!"0".equals(productionOrder.getStatus())) {
                throw new IllegalStateException("只有待生产的生产单才能开始生产");
            }

            XfptProductionOrder update = new XfptProductionOrder();
            update.setId(id);
            update.setStatus("1");
            update.setStartTime(new Date());
            int rows = xfptProductionOrderMapper.updateById(update);

            xfptProductionOrderItemService.lambdaUpdate()
                    .eq(XfptProductionOrderItem::getProductionId, id)
                    .eq(XfptProductionOrderItem::getDelFlag, "0")
                    .eq(XfptProductionOrderItem::getStatus, "0")
                    .set(XfptProductionOrderItem::getStatus, "1")
                    .update();
            return rows;
        }

        /**
         * 完成生产
         *
         * @param id 生产单主键
         * @return 结果
         */
        @Transactional
        @Override
        public int finishProduction(Long id)
        {
            XfptProductionOrder productionOrder = getUsableProductionOrder(id);
            if (!"1".equals(productionOrder.getStatus())) {
                throw new IllegalStateException("只有生产中的生产单才能完成生产");
            }

            XfptProductionOrder update = new XfptProductionOrder();
            update.setId(id);
            update.setStatus("2");
            update.setFinishTime(new Date());
            int rows = xfptProductionOrderMapper.updateById(update);

            xfptProductionOrderItemService.lambdaUpdate()
                    .eq(XfptProductionOrderItem::getProductionId, id)
                    .eq(XfptProductionOrderItem::getDelFlag, "0")
                    .set(XfptProductionOrderItem::getStatus, "2")
                    .setSql("finish_num = buy_num")
                    .update();

            if (productionOrder.getOrderId() != null) {
                xfptOrderInfoService.lambdaUpdate()
                        .eq(XfptOrderInfo::getId, productionOrder.getOrderId())
                        .eq(XfptOrderInfo::getDelFlag, "0")
                        .eq(XfptOrderInfo::getOrderStatus, "1")
                        .set(XfptOrderInfo::getOrderStatus, "2")
                        .update();
            }
            return rows;
        }

        /**
         * 修改生产单
         *
         * @param xfptProductionOrder 生产单
         * @return 结果
         */
        @Transactional
        @Override
        public int updateXfptProductionOrder(XfptProductionOrder xfptProductionOrder)
        {
                xfptProductionOrderMapper.deleteXfptProductionOrderItemByProductionId(xfptProductionOrder.getId());
            insertXfptProductionOrderItem(xfptProductionOrder);
            return xfptProductionOrderMapper.updateById(xfptProductionOrder);
        }

        /**
         * 批量删除生产单
         *
         * @param ids 需要删除的生产单主键
         * @return 结果
         */
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

        /**
         * 删除生产单信息
         *
         * @param id 生产单主键
         * @return 结果
         */
        @Transactional
        @Override
        public int deleteXfptProductionOrderById(Long id)
        {
                xfptProductionOrderMapper.deleteXfptProductionOrderItemByProductionId(id);
            return xfptProductionOrderMapper.deleteById(id);
        }

        /**
         * 新增生产单明细信息
         *
         * @param xfptProductionOrder 生产单对象
         */
        private void insertXfptProductionOrderItem(XfptProductionOrder xfptProductionOrder)
        {
            List<XfptProductionOrderItem> xfptProductionOrderItemList = xfptProductionOrder.getXfptProductionOrderItemList();
            Long id = xfptProductionOrder.getId();
            if (StringUtils.isNotNull(xfptProductionOrderItemList)) {
                for (XfptProductionOrderItem xfptProductionOrderItem : xfptProductionOrderItemList) {
                    xfptProductionOrderItem.setProductionId(id);
                    if (StringUtils.isEmpty(xfptProductionOrderItem.getStatus())) {
                        xfptProductionOrderItem.setStatus("0");
                    }
                    if (xfptProductionOrderItem.getFinishNum() == null) {
                        xfptProductionOrderItem.setFinishNum(0);
                    }
                    if (StringUtils.isEmpty(xfptProductionOrderItem.getDelFlag())) {
                        xfptProductionOrderItem.setDelFlag("0");
                    }
                }
                    xfptProductionOrderMapper.batchInsertXfptProductionOrderItem(xfptProductionOrderItemList);
            }
        }

        private List<XfptProductionOrderItem> buildProductionOrderItems(List<XfptOrderItem> orderItems)
        {
            List<XfptProductionOrderItem> productionOrderItems = new ArrayList<>();
            for (XfptOrderItem orderItem : orderItems) {
                XfptProductionOrderItem productionOrderItem = new XfptProductionOrderItem();
                productionOrderItem.setOrderItemId(orderItem.getId());
                productionOrderItem.setGoodsId(orderItem.getGoodsId());
                productionOrderItem.setSizeId(orderItem.getSizeId());
                productionOrderItem.setGoodsName(orderItem.getGoodsName());
                productionOrderItem.setSizeName(orderItem.getSizeName());
                productionOrderItem.setBuyNum(orderItem.getBuyNum());
                productionOrderItem.setFinishNum(0);
                productionOrderItem.setStatus("0");
                productionOrderItem.setDelFlag("0");
                productionOrderItems.add(productionOrderItem);
            }
            return productionOrderItems;
        }

        private String buildProductionNo(Long orderId)
        {
            return "PROD-" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "-" + orderId;
        }

        private XfptProductionOrder getUsableProductionOrder(Long id)
        {
            if (id == null) {
                throw new IllegalArgumentException("生产单ID不能为空");
            }
            XfptProductionOrder productionOrder = xfptProductionOrderMapper.selectById(id);
            if (productionOrder == null || "1".equals(productionOrder.getDelFlag())) {
                throw new IllegalArgumentException("生产单不存在或已删除");
            }
            return productionOrder;
        }

}
