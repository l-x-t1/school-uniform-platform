package com.xfgl.school.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.XfptSuppliceContractSon;

import java.util.List;

/**
 * 学校与供应商采购合同从Service接口
 *
 * @author yinet
 * @date 2025-04-22
 */
public interface IXfptSuppliceContractSonService extends IService<XfptSuppliceContractSon> {

    /**
     * 查询学校与供应商采购合同从列表
     *
     * @param xfptSuppliceContractSon 学校与供应商采购合同从
     * @return 学校与供应商采购合同从集合
     */
    public List<XfptSuppliceContractSon> selectXfptSuppliceContractSonList(XfptSuppliceContractSon xfptSuppliceContractSon);

}
