package com.xfgl.supplier.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import com.xfgl.supplier.mapper.XfptFinanceFlowMapper;
import com.xfgl.supplier.domain.XfptFinanceFlow;
import com.xfgl.supplier.service.IXfptFinanceFlowService;

/**
 * 财务流水Service业务层处理
 *
 * @author yinet
 * @date 2026-05-01
 */
@Service
public class XfptFinanceFlowServiceImpl extends ServiceImpl<XfptFinanceFlowMapper, XfptFinanceFlow> implements IXfptFinanceFlowService {

    @Autowired
    private XfptFinanceFlowMapper xfptFinanceFlowMapper;

    /**
     * 查询财务流水列表
     *
     * @param xfptFinanceFlow 财务流水
     * @return 财务流水
     */
    @Override
    public List<XfptFinanceFlow> selectXfptFinanceFlowList(XfptFinanceFlow xfptFinanceFlow)
    {
        return xfptFinanceFlowMapper.selectXfptFinanceFlowList(xfptFinanceFlow);
    }


}
