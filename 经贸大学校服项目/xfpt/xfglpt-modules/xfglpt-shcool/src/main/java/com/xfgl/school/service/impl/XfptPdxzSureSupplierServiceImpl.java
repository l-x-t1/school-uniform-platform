package com.xfgl.school.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import com.xfgl.school.mapper.XfptPdxzSureSupplierMapper;
import com.xfgl.school.domain.XfptPdxzSureSupplier;
import com.xfgl.school.service.IXfptPdxzSureSupplierService;

/**
 * 评定小组确定供应商Service业务层处理
 *
 * @author yinet
 * @date 2025-05-23
 */
@Service
public class XfptPdxzSureSupplierServiceImpl extends ServiceImpl<XfptPdxzSureSupplierMapper, XfptPdxzSureSupplier> implements IXfptPdxzSureSupplierService {

    @Autowired
    private XfptPdxzSureSupplierMapper xfptPdxzSureSupplierMapper;

    /**
     * 查询评定小组确定供应商列表
     *
     * @param xfptPdxzSureSupplier 评定小组确定供应商
     * @return 评定小组确定供应商
     */
    @Override
    public List<XfptPdxzSureSupplier> selectXfptPdxzSureSupplierList(XfptPdxzSureSupplier xfptPdxzSureSupplier)
    {
        return xfptPdxzSureSupplierMapper.selectXfptPdxzSureSupplierList(xfptPdxzSureSupplier);
    }


}
