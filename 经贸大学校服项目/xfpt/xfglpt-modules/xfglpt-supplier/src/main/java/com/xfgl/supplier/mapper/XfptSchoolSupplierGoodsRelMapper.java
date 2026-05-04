package com.xfgl.supplier.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.supplier.domain.XfptSchoolSupplierGoodsRel;

/**
 * 学校供应商商品关系Mapper接口
 *
 * @author yinet
 * @date 2026-05-01
 */
public interface XfptSchoolSupplierGoodsRelMapper extends BaseMapper<XfptSchoolSupplierGoodsRel> {
    /**
     * 查询学校供应商商品关系列表
     *
     * @param xfptSchoolSupplierGoodsRel 学校供应商商品关系
     * @return 学校供应商商品关系集合
     */
    public List<XfptSchoolSupplierGoodsRel> selectXfptSchoolSupplierGoodsRelList(XfptSchoolSupplierGoodsRel xfptSchoolSupplierGoodsRel);
}
