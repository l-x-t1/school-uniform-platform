package com.xfgl.school.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.school.domain.XfptSuppliceContractSon;

import java.util.List;

/**
 * 学校与供应商采购合同从Mapper接口
 *
 * @author yinet
 * @date 2025-04-22
 */
public interface XfptSuppliceContractSonMapper extends BaseMapper<XfptSuppliceContractSon> {
    /**
     * 查询学校与供应商采购合同从列表
     *
     * @param xfptSuppliceContractSon 学校与供应商采购合同从
     * @return 学校与供应商采购合同从集合
     */
    public List<XfptSuppliceContractSon> selectXfptSuppliceContractSonList(XfptSuppliceContractSon xfptSuppliceContractSon);
}
