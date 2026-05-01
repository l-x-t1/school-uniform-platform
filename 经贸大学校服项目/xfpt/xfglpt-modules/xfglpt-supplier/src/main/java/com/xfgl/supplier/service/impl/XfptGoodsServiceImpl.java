package com.xfgl.supplier.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import com.xfgl.supplier.mapper.XfptGoodsMapper;
import com.xfgl.supplier.domain.XfptGoods;
import com.xfgl.supplier.service.IXfptGoodsService;

/**
 * 商品信息Service业务层处理
 *
 * @author yinet
 * @date 2026-05-01
 */
@Service
public class XfptGoodsServiceImpl extends ServiceImpl<XfptGoodsMapper, XfptGoods> implements IXfptGoodsService {

    @Autowired
    private XfptGoodsMapper xfptGoodsMapper;

    /**
     * 查询商品信息列表
     *
     * @param xfptGoods 商品信息
     * @return 商品信息
     */
    @Override
    public List<XfptGoods> selectXfptGoodsList(XfptGoods xfptGoods)
    {
        return xfptGoodsMapper.selectXfptGoodsList(xfptGoods);
    }


}
