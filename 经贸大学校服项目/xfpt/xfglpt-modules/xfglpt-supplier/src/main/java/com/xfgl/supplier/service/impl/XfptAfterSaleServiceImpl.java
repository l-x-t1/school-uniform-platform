package com.xfgl.supplier.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import com.xfgl.supplier.mapper.XfptAfterSaleMapper;
import com.xfgl.supplier.domain.XfptAfterSale;
import com.xfgl.supplier.service.IXfptAfterSaleService;

/**
 * 售后单Service业务层处理
 *
 * @author yinet
 * @date 2026-05-01
 */
@Service
public class XfptAfterSaleServiceImpl extends ServiceImpl<XfptAfterSaleMapper, XfptAfterSale> implements IXfptAfterSaleService {

    @Autowired
    private XfptAfterSaleMapper xfptAfterSaleMapper;

    /**
     * 查询售后单列表
     *
     * @param xfptAfterSale 售后单
     * @return 售后单
     */
    @Override
    public List<XfptAfterSale> selectXfptAfterSaleList(XfptAfterSale xfptAfterSale)
    {
        return xfptAfterSaleMapper.selectXfptAfterSaleList(xfptAfterSale);
    }


}
