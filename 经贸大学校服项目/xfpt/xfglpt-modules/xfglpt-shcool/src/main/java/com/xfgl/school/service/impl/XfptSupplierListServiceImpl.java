package com.xfgl.school.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import com.xfgl.school.mapper.XfptSupplierListMapper;
import com.xfgl.school.domain.XfptSupplierList;
import com.xfgl.school.service.IXfptSupplierListService;

/**
 * 确定供应商名单Service业务层处理
 *
 * @author yinet
 * @date 2025-05-23
 */
@Service
public class XfptSupplierListServiceImpl extends ServiceImpl<XfptSupplierListMapper, XfptSupplierList> implements IXfptSupplierListService {

    @Autowired
    private XfptSupplierListMapper xfptSupplierListMapper;

    /**
     * 查询确定供应商名单列表
     *
     * @param xfptSupplierList 确定供应商名单
     * @return 确定供应商名单
     */
    @Override
    public List<XfptSupplierList> selectXfptSupplierListList(XfptSupplierList xfptSupplierList)
    {
        return xfptSupplierListMapper.selectXfptSupplierListList(xfptSupplierList);
    }


}
