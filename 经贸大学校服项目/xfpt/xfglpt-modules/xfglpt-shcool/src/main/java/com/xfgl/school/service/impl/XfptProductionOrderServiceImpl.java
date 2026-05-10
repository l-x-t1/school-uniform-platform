package com.xfgl.school.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import com.xfgl.common.core.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.xfgl.school.domain.XfptProductionOrderItem;
import com.xfgl.school.mapper.XfptProductionOrderMapper;
import com.xfgl.school.domain.XfptProductionOrder;
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
                xfptProductionOrder.setOrderType("1");
            }
            if (StringUtils.isEmpty(xfptProductionOrder.getStatus())) {
                xfptProductionOrder.setStatus("0");
            }
            int rows = xfptProductionOrderMapper.insert(xfptProductionOrder);
            insertXfptProductionOrderItem(xfptProductionOrder);
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
                }
                    xfptProductionOrderMapper.batchInsertXfptProductionOrderItem(xfptProductionOrderItemList);
            }
        }

}
