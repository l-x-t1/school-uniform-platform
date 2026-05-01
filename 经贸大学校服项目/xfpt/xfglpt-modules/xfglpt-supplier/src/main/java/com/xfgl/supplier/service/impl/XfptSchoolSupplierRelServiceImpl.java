package com.xfgl.supplier.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import com.xfgl.supplier.mapper.XfptSchoolSupplierRelMapper;
import com.xfgl.supplier.domain.XfptSchoolSupplierRel;
import com.xfgl.supplier.service.IXfptSchoolSupplierRelService;

/**
 * 学校供应商合作关系Service业务层处理
 *
 * @author yinet
 * @date 2026-05-01
 */
@Service
public class XfptSchoolSupplierRelServiceImpl extends ServiceImpl<XfptSchoolSupplierRelMapper, XfptSchoolSupplierRel> implements IXfptSchoolSupplierRelService {

    @Autowired
    private XfptSchoolSupplierRelMapper xfptSchoolSupplierRelMapper;

    /**
     * 查询学校供应商合作关系列表
     *
     * @param xfptSchoolSupplierRel 学校供应商合作关系
     * @return 学校供应商合作关系
     */
    @Override
    public List<XfptSchoolSupplierRel> selectXfptSchoolSupplierRelList(XfptSchoolSupplierRel xfptSchoolSupplierRel)
    {
        return xfptSchoolSupplierRelMapper.selectXfptSchoolSupplierRelList(xfptSchoolSupplierRel);
    }


}
