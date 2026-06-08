package com.xfgl.school.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.school.domain.XfptSchoolSupplierGoodsRel;
import org.apache.ibatis.annotations.Param;

/**
 * 学校供应商合作商品Mapper接口
 *
 * @author yinet
 * @date 2026-05-04
 */
public interface XfptSchoolSupplierGoodsRelMapper extends BaseMapper<XfptSchoolSupplierGoodsRel> {
    /**
     * 查询学校供应商合作商品列表
     *
     * @param xfptSchoolSupplierGoodsRel 学校供应商合作商品
     * @return 学校供应商合作商品集合
     */
    public List<XfptSchoolSupplierGoodsRel> selectXfptSchoolSupplierGoodsRelList(XfptSchoolSupplierGoodsRel xfptSchoolSupplierGoodsRel);

    /**
     * 鏌ヨ瀛︽牎渚涘簲鍟嗗悎浣滃晢鍝佽缁嗭紙鍖呭惈灞曠ず鍚嶇О锛?
     *
     * @param id 涓婚敭
     * @return 鍚堜綔鍟嗗搧
     */
    XfptSchoolSupplierGoodsRel selectXfptSchoolSupplierGoodsRelById(@Param("id") Long id);
}
