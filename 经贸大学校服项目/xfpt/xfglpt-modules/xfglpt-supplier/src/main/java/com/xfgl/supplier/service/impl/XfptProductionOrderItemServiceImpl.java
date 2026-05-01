package com.xfgl.supplier.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import com.xfgl.supplier.mapper.XfptProductionOrderItemMapper;
import com.xfgl.supplier.domain.XfptProductionOrderItem;
import com.xfgl.supplier.service.IXfptProductionOrderItemService;

/**
 * 生产单明细Service业务层处理
 *
 * @author yinet
 * @date 2026-05-01
 */
@Service
public class XfptProductionOrderItemServiceImpl extends ServiceImpl<XfptProductionOrderItemMapper, XfptProductionOrderItem> implements IXfptProductionOrderItemService {

    @Autowired
    private XfptProductionOrderItemMapper xfptProductionOrderItemMapper;

    /**
     * 查询生产单明细列表
     *
     * @param xfptProductionOrderItem 生产单明细
     * @return 生产单明细
     */
    @Override
    public List<XfptProductionOrderItem> selectXfptProductionOrderItemList(XfptProductionOrderItem xfptProductionOrderItem)
    {
        return xfptProductionOrderItemMapper.selectXfptProductionOrderItemList(xfptProductionOrderItem);
    }


}
