package com.xfgl.school.service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.XfptSchoolSupplierGoodsRel;

/**
 * 学校供应商合作商品Service接口
 *
 * @author yinet
 * @date 2026-05-04
 */
public interface IXfptSchoolSupplierGoodsRelService extends IService<XfptSchoolSupplierGoodsRel> {

    /**
     * 查询学校供应商合作商品列表
     *
     * @param xfptSchoolSupplierGoodsRel 学校供应商合作商品
     * @return 学校供应商合作商品集合
     */
    public List<XfptSchoolSupplierGoodsRel> selectXfptSchoolSupplierGoodsRelList(XfptSchoolSupplierGoodsRel xfptSchoolSupplierGoodsRel);

}
