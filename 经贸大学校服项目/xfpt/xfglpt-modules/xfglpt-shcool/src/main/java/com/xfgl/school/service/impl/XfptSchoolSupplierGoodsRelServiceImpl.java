package com.xfgl.school.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import com.xfgl.school.mapper.XfptSchoolSupplierGoodsRelMapper;
import com.xfgl.school.domain.XfptSchoolSupplierGoodsRel;
import com.xfgl.school.service.IXfptSchoolSupplierGoodsRelService;

/**
 * 学校供应商合作商品Service业务层处理
 *
 * @author yinet
 * @date 2026-05-04
 */
@Service
public class XfptSchoolSupplierGoodsRelServiceImpl extends ServiceImpl<XfptSchoolSupplierGoodsRelMapper, XfptSchoolSupplierGoodsRel> implements IXfptSchoolSupplierGoodsRelService {

    @Autowired
    private XfptSchoolSupplierGoodsRelMapper xfptSchoolSupplierGoodsRelMapper;

    /**
     * 查询学校供应商合作商品列表
     *
     * @param xfptSchoolSupplierGoodsRel 学校供应商合作商品
     * @return 学校供应商合作商品
     */
    @Override
    public List<XfptSchoolSupplierGoodsRel> selectXfptSchoolSupplierGoodsRelList(XfptSchoolSupplierGoodsRel xfptSchoolSupplierGoodsRel)
    {
        return xfptSchoolSupplierGoodsRelMapper.selectXfptSchoolSupplierGoodsRelList(xfptSchoolSupplierGoodsRel);
    }


}
