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
import com.xfgl.school.domain.XfptOrderItem;
import com.xfgl.school.mapper.XfptOrderInfoMapper;
import com.xfgl.school.domain.XfptOrderInfo;
import com.xfgl.school.service.IXfptOrderInfoService;

/**
 * 订单信息Service业务层处理
 *
 * @author yinet
 * @date 2026-05-04
 */
@Service
public class XfptOrderInfoServiceImpl extends ServiceImpl<XfptOrderInfoMapper, XfptOrderInfo> implements IXfptOrderInfoService {

    @Autowired
    private XfptOrderInfoMapper xfptOrderInfoMapper;

    /**
     * 查询订单信息列表
     *
     * @param xfptOrderInfo 订单信息
     * @return 订单信息
     */
    @Override
    public List<XfptOrderInfo> selectXfptOrderInfoList(XfptOrderInfo xfptOrderInfo)
    {
        return xfptOrderInfoMapper.selectXfptOrderInfoList(xfptOrderInfo);
    }

        /**
         * 查询订单信息
         *
         * @param id 订单信息主键
         * @return 订单信息
         */
        @Override
        public XfptOrderInfo selectXfptOrderInfoById(Long id)
        {
            XfptOrderInfo xfptOrderInfo = xfptOrderInfoMapper.selectById(id);
            List<XfptOrderItem> xfptOrderItemList = xfptOrderInfoMapper.selectXfptOrderItemList(id);
            xfptOrderInfo.setXfptOrderItemList(xfptOrderItemList);
            return xfptOrderInfo;
        }

        /**
         * 新增订单信息
         *
         * @param xfptOrderInfo 订单信息
         * @return 结果
         */
        @Transactional
        @Override
        public int insertXfptOrderInfo(XfptOrderInfo xfptOrderInfo)
        {
            int rows = xfptOrderInfoMapper.insert(xfptOrderInfo);
            insertXfptOrderItem(xfptOrderInfo);
            return rows;
        }

        /**
         * 修改订单信息
         *
         * @param xfptOrderInfo 订单信息
         * @return 结果
         */
        @Transactional
        @Override
        public int updateXfptOrderInfo(XfptOrderInfo xfptOrderInfo)
        {
                xfptOrderInfoMapper.deleteXfptOrderItemByOrderId(xfptOrderInfo.getId());
            insertXfptOrderItem(xfptOrderInfo);
            return xfptOrderInfoMapper.updateById(xfptOrderInfo);
        }

        /**
         * 批量删除订单信息
         *
         * @param ids 需要删除的订单信息主键
         * @return 结果
         */
        @Transactional
        @Override
        public int deleteXfptOrderInfoByIds(Long[] ids)
        {
            List<Long> idList = Arrays.asList(ids);
            for (Long id : idList) {
                xfptOrderInfoMapper.deleteXfptOrderItemByOrderId(id);
        }
            return xfptOrderInfoMapper.deleteBatchIds(idList);
        }

        /**
         * 删除订单信息信息
         *
         * @param id 订单信息主键
         * @return 结果
         */
        @Transactional
        @Override
        public int deleteXfptOrderInfoById(Long id)
        {
                xfptOrderInfoMapper.deleteXfptOrderItemByOrderId(id);
            return xfptOrderInfoMapper.deleteById(id);
        }

        /**
         * 新增订单明细信息
         *
         * @param xfptOrderInfo 订单信息对象
         */
        private void insertXfptOrderItem(XfptOrderInfo xfptOrderInfo)
        {
            List<XfptOrderItem> xfptOrderItemList = xfptOrderInfo.getXfptOrderItemList();
            Long id = xfptOrderInfo.getId();
            if (StringUtils.isNotNull(xfptOrderItemList)) {
                for (XfptOrderItem xfptOrderItem : xfptOrderItemList) {
                    // 订单明细主键由数据库自增生成，避免把购物车/商品ID误当成明细ID插入
                    xfptOrderItem.setId(null);
                    xfptOrderItem.setOrderId(id);
                    fillOrderItemDefaults(xfptOrderItem, xfptOrderInfo.getOrderType());
                }
                    xfptOrderInfoMapper.batchInsertXfptOrderItem(xfptOrderItemList);
            }
        }

        private void fillOrderItemDefaults(XfptOrderItem xfptOrderItem, String orderType)
        {
            if (xfptOrderItem.getSaleType() == null || "".equals(xfptOrderItem.getSaleType())) {
                if (orderType != null && !"".equals(orderType)) {
                    xfptOrderItem.setSaleType(orderType);
                } else {
                    xfptOrderItem.setSaleType("1");
                }
            }
            if (xfptOrderItem.getDelFlag() == null || "".equals(xfptOrderItem.getDelFlag())) {
                xfptOrderItem.setDelFlag("0");
            }
        }

}
