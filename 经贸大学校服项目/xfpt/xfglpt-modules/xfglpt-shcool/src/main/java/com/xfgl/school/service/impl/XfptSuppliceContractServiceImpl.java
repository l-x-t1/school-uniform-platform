package com.xfgl.school.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xfgl.school.domain.XfptSuppliceContract;
import com.xfgl.school.mapper.XfptSuppliceContractMapper;
import com.xfgl.school.service.IXfptSuppliceContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 学校与供应商采购合同关联Service业务层处理
 *
 * @author yinet
 * @date 2025-04-22
 */
@Service
public class XfptSuppliceContractServiceImpl extends ServiceImpl<XfptSuppliceContractMapper, XfptSuppliceContract> implements IXfptSuppliceContractService {

    @Autowired
    private XfptSuppliceContractMapper xfptSuppliceContractMapper;

    /**
     * 查询学校与供应商采购合同关联列表
     *
     * @param xfptSuppliceContract 学校与供应商采购合同关联
     * @return 学校与供应商采购合同关联
     */
    @Override
    public List<XfptSuppliceContract> selectXfptSuppliceContractList(XfptSuppliceContract xfptSuppliceContract)
    {
        return xfptSuppliceContractMapper.selectXfptSuppliceContractList(xfptSuppliceContract);
    }


}
