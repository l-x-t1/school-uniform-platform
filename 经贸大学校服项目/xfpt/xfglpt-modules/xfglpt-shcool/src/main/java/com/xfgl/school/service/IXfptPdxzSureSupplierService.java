package com.xfgl.school.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.XfptPdxzSureSupplier;

/**
 * 评定小组确定供应商Service接口
 *
 * @author yinet
 * @date 2025-05-23
 */
public interface IXfptPdxzSureSupplierService extends IService<XfptPdxzSureSupplier> {

    /**
     * 查询评定小组确定供应商列表
     *
     * @param xfptPdxzSureSupplier 评定小组确定供应商
     * @return 评定小组确定供应商集合
     */
    public List<XfptPdxzSureSupplier> selectXfptPdxzSureSupplierList(XfptPdxzSureSupplier xfptPdxzSureSupplier);

}
