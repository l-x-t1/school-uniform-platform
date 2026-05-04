package com.xfgl.supplier.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import com.xfgl.supplier.mapper.XfptProductionOrderMapper;
import com.xfgl.supplier.domain.XfptProductionOrder;
import com.xfgl.supplier.service.IXfptProductionOrderService;

/**
 * 生产单主Service业务层处理
 *
 * @author yinet
 * @date 2026-05-01
 */
@Service
public class XfptProductionOrderServiceImpl extends ServiceImpl<XfptProductionOrderMapper, XfptProductionOrder> implements IXfptProductionOrderService {

    @Autowired
    private XfptProductionOrderMapper xfptProductionOrderMapper;

    /**
     * 查询生产单主列表
     *
     * @param xfptProductionOrder 生产单主
     * @return 生产单主
     */
    @Override
    public List<XfptProductionOrder> selectXfptProductionOrderList(XfptProductionOrder xfptProductionOrder)
    {
        return xfptProductionOrderMapper.selectXfptProductionOrderList(xfptProductionOrder);
    }


}
