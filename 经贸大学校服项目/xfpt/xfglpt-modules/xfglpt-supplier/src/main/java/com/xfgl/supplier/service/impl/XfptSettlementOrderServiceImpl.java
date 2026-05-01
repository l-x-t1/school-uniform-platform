package com.xfgl.supplier.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import com.xfgl.supplier.mapper.XfptSettlementOrderMapper;
import com.xfgl.supplier.domain.XfptSettlementOrder;
import com.xfgl.supplier.service.IXfptSettlementOrderService;

/**
 * 结算单Service业务层处理
 *
 * @author yinet
 * @date 2026-05-01
 */
@Service
public class XfptSettlementOrderServiceImpl extends ServiceImpl<XfptSettlementOrderMapper, XfptSettlementOrder> implements IXfptSettlementOrderService {

    @Autowired
    private XfptSettlementOrderMapper xfptSettlementOrderMapper;

    /**
     * 查询结算单列表
     *
     * @param xfptSettlementOrder 结算单
     * @return 结算单
     */
    @Override
    public List<XfptSettlementOrder> selectXfptSettlementOrderList(XfptSettlementOrder xfptSettlementOrder)
    {
        return xfptSettlementOrderMapper.selectXfptSettlementOrderList(xfptSettlementOrder);
    }


}
