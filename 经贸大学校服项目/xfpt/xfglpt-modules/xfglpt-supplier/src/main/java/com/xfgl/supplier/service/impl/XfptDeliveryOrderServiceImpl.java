package com.xfgl.supplier.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import com.xfgl.supplier.mapper.XfptDeliveryOrderMapper;
import com.xfgl.supplier.domain.XfptDeliveryOrder;
import com.xfgl.supplier.service.IXfptDeliveryOrderService;

/**
 * 发货单Service业务层处理
 *
 * @author yinet
 * @date 2026-05-01
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


}
