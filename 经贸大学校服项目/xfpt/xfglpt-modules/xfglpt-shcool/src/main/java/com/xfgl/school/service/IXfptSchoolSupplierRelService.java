package com.xfgl.school.service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.XfptSchoolSupplierGoodsRel;
import com.xfgl.school.domain.XfptSchoolSupplierRel;

/**
 * 学校供应商合作关系Service接口
 *
 * @author yinet
 * @date 2026-05-04
 */
public interface IXfptSchoolSupplierRelService extends IService<XfptSchoolSupplierRel> {

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
        public XfptSchoolSupplierRel selectXfptSchoolSupplierRelById(Long id);

        /**
         * 新增学校供应商合作关系
         *
         * @param xfptSchoolSupplierRel 学校供应商合作关系
         * @return 结果
         */
        public int insertXfptSchoolSupplierRel(XfptSchoolSupplierRel xfptSchoolSupplierRel);

        /**
         * 修改学校供应商合作关系
         *
         * @param xfptSchoolSupplierRel 学校供应商合作关系
         * @return 结果
         */
        public int updateXfptSchoolSupplierRel(XfptSchoolSupplierRel xfptSchoolSupplierRel);

        /**
         * 取消合作
         *
         * @param id 学校供应商合作关系主键
         * @return 结果
         */
        public int cancelCooperation(Long id);

        /**
         * 设置服务电话
         *
         * @param id 学校供应商合作关系主键
         * @param servicePhone 服务电话
         * @return 结果
         */
        public int updateServicePhone(Long id, String servicePhone);

        /**
         * 替换合作商品
         *
         * @param id 学校供应商合作关系主键
         * @param goodsRelList 合作商品列表
         * @return 结果
         */
        public int replaceGoodsRel(Long id, List<XfptSchoolSupplierGoodsRel> goodsRelList);

        /**
         * 批量删除学校供应商合作关系
         *
         * @param ids 需要删除的学校供应商合作关系主键集合
         * @return 结果
         */
        public int deleteXfptSchoolSupplierRelByIds(Long[] ids);

        /**
         * 删除学校供应商合作关系信息
         *
         * @param id 学校供应商合作关系主键
         * @return 结果
         */
        public int deleteXfptSchoolSupplierRelById(Long id);
}
