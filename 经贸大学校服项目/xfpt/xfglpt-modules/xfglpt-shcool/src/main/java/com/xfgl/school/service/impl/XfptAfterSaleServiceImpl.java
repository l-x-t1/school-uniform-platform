package com.xfgl.school.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import com.xfgl.school.mapper.XfptAfterSaleMapper;
import com.xfgl.school.domain.XfptAfterSale;
import com.xfgl.school.service.IXfptAfterSaleService;

/**
 * 售后主Service业务层处理
 *
 * @author yinet
 * @date 2026-05-04
 */
@Service
public class XfptAfterSaleServiceImpl extends ServiceImpl<XfptAfterSaleMapper, XfptAfterSale> implements IXfptAfterSaleService {

    @Autowired
    private XfptAfterSaleMapper xfptAfterSaleMapper;

    /**
     * 查询售后主列表
     *
     * @param xfptAfterSale 售后主
     * @return 售后主
     */
    @Override
    public List<XfptAfterSale> selectXfptAfterSaleList(XfptAfterSale xfptAfterSale)
    {
        return xfptAfterSaleMapper.selectXfptAfterSaleList(xfptAfterSale);
    }


}
