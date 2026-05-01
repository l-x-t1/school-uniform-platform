package com.xfgl.supplier.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import com.xfgl.supplier.mapper.XfptGoodsSizeMapper;
import com.xfgl.supplier.domain.XfptGoodsSize;
import com.xfgl.supplier.service.IXfptGoodsSizeService;

/**
 * 商品尺码Service业务层处理
 *
 * @author yinet
 * @date 2026-05-01
 */
@Service
public class XfptGoodsSizeServiceImpl extends ServiceImpl<XfptGoodsSizeMapper, XfptGoodsSize> implements IXfptGoodsSizeService {

    @Autowired
    private XfptGoodsSizeMapper xfptGoodsSizeMapper;

    /**
     * 查询商品尺码列表
     *
     * @param xfptGoodsSize 商品尺码
     * @return 商品尺码
     */
    @Override
    public List<XfptGoodsSize> selectXfptGoodsSizeList(XfptGoodsSize xfptGoodsSize)
    {
        return xfptGoodsSizeMapper.selectXfptGoodsSizeList(xfptGoodsSize);
    }


}
