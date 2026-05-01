package com.xfgl.school.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xfgl.school.domain.XfptSuppliceContractSon;
import com.xfgl.school.mapper.XfptSuppliceContractSonMapper;
import com.xfgl.school.service.IXfptSuppliceContractSonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 学校与供应商采购合同从Service业务层处理
 *
 * @author yinet
 * @date 2025-04-22
 */
@Service
public class XfptSuppliceContractSonServiceImpl extends ServiceImpl<XfptSuppliceContractSonMapper, XfptSuppliceContractSon> implements IXfptSuppliceContractSonService {

    @Autowired
    private XfptSuppliceContractSonMapper xfptSuppliceContractSonMapper;

    /**
     * 查询学校与供应商采购合同从列表
     *
     * @param xfptSuppliceContractSon 学校与供应商采购合同从
     * @return 学校与供应商采购合同从
     */
    @Override
    public List<XfptSuppliceContractSon> selectXfptSuppliceContractSonList(XfptSuppliceContractSon xfptSuppliceContractSon)
    {
        return xfptSuppliceContractSonMapper.selectXfptSuppliceContractSonList(xfptSuppliceContractSon);
    }


}
