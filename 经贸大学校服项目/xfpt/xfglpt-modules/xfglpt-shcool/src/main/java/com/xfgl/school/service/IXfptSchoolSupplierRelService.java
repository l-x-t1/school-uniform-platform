package com.xfgl.school.service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
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
