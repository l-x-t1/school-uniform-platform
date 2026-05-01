package com.xfgl.supplier.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import com.xfgl.supplier.mapper.XfptOrderItemMapper;
import com.xfgl.supplier.domain.XfptOrderItem;
import com.xfgl.supplier.service.IXfptOrderItemService;

/**
 * 订单明细Service业务层处理
 *
 * @author yinet
 * @date 2026-05-01
 */
@Service
public class XfptOrderItemServiceImpl extends ServiceImpl<XfptOrderItemMapper, XfptOrderItem> implements IXfptOrderItemService {

    @Autowired
    private XfptOrderItemMapper xfptOrderItemMapper;

    /**
     * 查询订单明细列表
     *
     * @param xfptOrderItem 订单明细
     * @return 订单明细
     */
    @Override
    public List<XfptOrderItem> selectXfptOrderItemList(XfptOrderItem xfptOrderItem)
    {
        return xfptOrderItemMapper.selectXfptOrderItemList(xfptOrderItem);
    }


}
