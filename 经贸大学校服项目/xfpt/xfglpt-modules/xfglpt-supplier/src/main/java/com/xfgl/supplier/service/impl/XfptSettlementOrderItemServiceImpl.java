package com.xfgl.supplier.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import com.xfgl.supplier.mapper.XfptSettlementOrderItemMapper;
import com.xfgl.supplier.domain.XfptSettlementOrderItem;
import com.xfgl.supplier.service.IXfptSettlementOrderItemService;

/**
 * 结算明细Service业务层处理
 *
 * @author yinet
 * @date 2026-05-01
 */
@Service
public class XfptSettlementOrderItemServiceImpl extends ServiceImpl<XfptSettlementOrderItemMapper, XfptSettlementOrderItem> implements IXfptSettlementOrderItemService {

    @Autowired
    private XfptSettlementOrderItemMapper xfptSettlementOrderItemMapper;

    /**
     * 查询结算明细列表
     *
     * @param xfptSettlementOrderItem 结算明细
     * @return 结算明细
     */
    @Override
    public List<XfptSettlementOrderItem> selectXfptSettlementOrderItemList(XfptSettlementOrderItem xfptSettlementOrderItem)
    {
        return xfptSettlementOrderItemMapper.selectXfptSettlementOrderItemList(xfptSettlementOrderItem);
    }


}
