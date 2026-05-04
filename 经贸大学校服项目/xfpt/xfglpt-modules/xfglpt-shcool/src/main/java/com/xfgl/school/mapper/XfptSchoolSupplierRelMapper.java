package com.xfgl.school.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.school.domain.XfptSchoolSupplierRel;
import org.apache.ibatis.annotations.Param;
import com.xfgl.school.domain.XfptSchoolSupplierGoodsRel;

/**
 * 学校供应商合作关系Mapper接口
 *
 * @author yinet
 * @date 2026-05-04
 */
public interface XfptSchoolSupplierRelMapper extends BaseMapper<XfptSchoolSupplierRel> {
    /**
     * 查询学校供应商合作关系列表
     *
     * @param xfptSchoolSupplierRel 学校供应商合作关系
     * @return 学校供应商合作关系集合
     */
    public List<XfptSchoolSupplierRel> selectXfptSchoolSupplierRelList(XfptSchoolSupplierRel xfptSchoolSupplierRel);
        /**
         * 查询学校供应商合作关系
         *
         * @param id 学校供应商合作关系主键
         * @return 学校供应商合作关系
         */
        List<XfptSchoolSupplierGoodsRel> selectXfptSchoolSupplierGoodsRelById(@Param("id")Long id);

        /**
         * 删除学校供应商合作关系信息
         *
         * @param id 学校供应商合作关系主键
         * @return 结果
         */
        int deleteXfptSchoolSupplierGoodsRelByRelId(@Param("id")Long id);

        /**
         * 批量新增学校供应商合作商品
         *
         * @param xfptSchoolSupplierGoodsRelList 学校供应商合作商品列表
         * @return 结果
         */
        int batchInsertXfptSchoolSupplierGoodsRel(List<XfptSchoolSupplierGoodsRel> xfptSchoolSupplierGoodsRelList);
}
