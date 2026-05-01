package com.xfgl.supplier.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.supplier.domain.XfptSchoolSupplierRel;

/**
 * 学校供应商合作关系Service接口
 *
 * @author yinet
 * @date 2026-05-01
 */
public interface IXfptSchoolSupplierRelService extends IService<XfptSchoolSupplierRel> {

    /**
     * 查询学校供应商合作关系列表
     *
     * @param xfptSchoolSupplierRel 学校供应商合作关系
     * @return 学校供应商合作关系集合
     */
    public List<XfptSchoolSupplierRel> selectXfptSchoolSupplierRelList(XfptSchoolSupplierRel xfptSchoolSupplierRel);

}
