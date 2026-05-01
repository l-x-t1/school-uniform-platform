package com.xfgl.school.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.school.domain.XfptSuppliceContract;

import java.util.List;

/**
 * 学校与供应商采购合同关联Mapper接口
 *
 * @author yinet
 * @date 2025-04-22
 */
public interface XfptSuppliceContractMapper extends BaseMapper<XfptSuppliceContract> {
    /**
     * 查询学校与供应商采购合同关联列表
     *
     * @param xfptSuppliceContract 学校与供应商采购合同关联
     * @return 学校与供应商采购合同关联集合
     */
    public List<XfptSuppliceContract> selectXfptSuppliceContractList(XfptSuppliceContract xfptSuppliceContract);
}
