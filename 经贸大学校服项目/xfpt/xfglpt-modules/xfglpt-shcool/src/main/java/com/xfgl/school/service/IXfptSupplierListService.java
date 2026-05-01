package com.xfgl.school.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.XfptSupplierList;

/**
 * 确定供应商名单Service接口
 *
 * @author yinet
 * @date 2025-05-23
 */
public interface IXfptSupplierListService extends IService<XfptSupplierList> {

    /**
     * 查询确定供应商名单列表
     *
     * @param xfptSupplierList 确定供应商名单
     * @return 确定供应商名单集合
     */
    public List<XfptSupplierList> selectXfptSupplierListList(XfptSupplierList xfptSupplierList);

}
