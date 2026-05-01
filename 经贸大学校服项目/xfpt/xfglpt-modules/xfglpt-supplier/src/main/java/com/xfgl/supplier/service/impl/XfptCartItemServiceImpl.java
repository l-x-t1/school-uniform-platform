package com.xfgl.supplier.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import com.xfgl.supplier.mapper.XfptCartItemMapper;
import com.xfgl.supplier.domain.XfptCartItem;
import com.xfgl.supplier.service.IXfptCartItemService;

/**
 * 购物车明细Service业务层处理
 *
 * @author yinet
 * @date 2026-05-01
 */
@Service
public class XfptCartItemServiceImpl extends ServiceImpl<XfptCartItemMapper, XfptCartItem> implements IXfptCartItemService {

    @Autowired
    private XfptCartItemMapper xfptCartItemMapper;

    /**
     * 查询购物车明细列表
     *
     * @param xfptCartItem 购物车明细
     * @return 购物车明细
     */
    @Override
    public List<XfptCartItem> selectXfptCartItemList(XfptCartItem xfptCartItem)
    {
        return xfptCartItemMapper.selectXfptCartItemList(xfptCartItem);
    }


}
