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
import com.xfgl.school.domain.XfptDeliveryOrderItem;
import com.xfgl.school.mapper.XfptDeliveryOrderMapper;
import com.xfgl.school.domain.XfptDeliveryOrder;
import com.xfgl.school.service.IXfptDeliveryOrderService;

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

    /**
     * 查询发货单列表
     *
     * @param xfptDeliveryOrder 发货单
     * @return 发货单
     */
    @Override
    public List<XfptDeliveryOrder> selectXfptDeliveryOrderList(XfptDeliveryOrder xfptDeliveryOrder)
    {
        return xfptDeliveryOrderMapper.selectXfptDeliveryOrderList(xfptDeliveryOrder);
    }

        /**
         * 查询发货单
         *
         * @param id 发货单主键
         * @return 发货单
         */
        @Override
        public XfptDeliveryOrder selectXfptDeliveryOrderById(Long id)
        {
            XfptDeliveryOrder xfptDeliveryOrder = xfptDeliveryOrderMapper.selectById(id);
            List<XfptDeliveryOrderItem> xfptDeliveryOrderItemList = xfptDeliveryOrderMapper.selectXfptDeliveryOrderItemById(id);
            xfptDeliveryOrder.setXfptDeliveryOrderItemList(xfptDeliveryOrderItemList);
            return xfptDeliveryOrder;
        }

        /**
         * 新增发货单
         *
         * @param xfptDeliveryOrder 发货单
         * @return 结果
         */
        @Transactional
        @Override
        public int insertXfptDeliveryOrder(XfptDeliveryOrder xfptDeliveryOrder)
        {
            if (StringUtils.isEmpty(xfptDeliveryOrder.getDeliveryStatus())) {
                xfptDeliveryOrder.setDeliveryStatus("0");
            }
            int rows = xfptDeliveryOrderMapper.insert(xfptDeliveryOrder);
            insertXfptDeliveryOrderItem(xfptDeliveryOrder);
            return rows;
        }

        /**
         * 修改发货单
         *
         * @param xfptDeliveryOrder 发货单
         * @return 结果
         */
        @Transactional
        @Override
        public int updateXfptDeliveryOrder(XfptDeliveryOrder xfptDeliveryOrder)
        {
                xfptDeliveryOrderMapper.deleteXfptDeliveryOrderItemByDeliveryId(xfptDeliveryOrder.getId());
            insertXfptDeliveryOrderItem(xfptDeliveryOrder);
            return xfptDeliveryOrderMapper.updateById(xfptDeliveryOrder);
        }

        /**
         * 批量删除发货单
         *
         * @param ids 需要删除的发货单主键
         * @return 结果
         */
        @Transactional
        @Override
        public int deleteXfptDeliveryOrderByIds(Long[] ids)
        {
            List<Long> idList = Arrays.asList(ids);
            for (Long id : idList) {
                xfptDeliveryOrderMapper.deleteXfptDeliveryOrderItemByDeliveryId(id);
        }
            return xfptDeliveryOrderMapper.deleteBatchIds(idList);
        }

        /**
         * 删除发货单信息
         *
         * @param id 发货单主键
         * @return 结果
         */
        @Transactional
        @Override
        public int deleteXfptDeliveryOrderById(Long id)
        {
                xfptDeliveryOrderMapper.deleteXfptDeliveryOrderItemByDeliveryId(id);
            return xfptDeliveryOrderMapper.deleteById(id);
        }

        /**
         * 新增发货单明细信息
         *
         * @param xfptDeliveryOrder 发货单对象
         */
        private void insertXfptDeliveryOrderItem(XfptDeliveryOrder xfptDeliveryOrder)
        {
            List<XfptDeliveryOrderItem> xfptDeliveryOrderItemList = xfptDeliveryOrder.getXfptDeliveryOrderItemList();
            Long id = xfptDeliveryOrder.getId();
            if (StringUtils.isNotNull(xfptDeliveryOrderItemList)) {
                for (XfptDeliveryOrderItem xfptDeliveryOrderItem : xfptDeliveryOrderItemList) {
                    xfptDeliveryOrderItem.setDeliveryId(id);
                }
                    xfptDeliveryOrderMapper.batchInsertXfptDeliveryOrderItem(xfptDeliveryOrderItemList);
            }
        }

}
