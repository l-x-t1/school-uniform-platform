package com.xfgl.school.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.XfptSuppliceContract;

import java.util.List;

/**
 * 学校与供应商采购合同关联Service接口
 *
 * @author yinet
 * @date 2025-04-22
 */
public interface IXfptSuppliceContractService extends IService<XfptSuppliceContract> {

    /**
     * 查询学校与供应商采购合同关联列表
     *
     * @param xfptSuppliceContract 学校与供应商采购合同关联
     * @return 学校与供应商采购合同关联集合
     */
    public List<XfptSuppliceContract> selectXfptSuppliceContractList(XfptSuppliceContract xfptSuppliceContract);

}
