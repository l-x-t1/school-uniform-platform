package com.xfgl.school.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import com.xfgl.school.mapper.XfptFinanceFlowMapper;
import com.xfgl.school.domain.XfptFinanceFlow;
import com.xfgl.school.service.IXfptFinanceFlowService;

/**
 * 资金流水Service业务层处理
 *
 * @author yinet
 * @date 2026-05-04
 */
@Service
public class XfptFinanceFlowServiceImpl extends ServiceImpl<XfptFinanceFlowMapper, XfptFinanceFlow> implements IXfptFinanceFlowService {

    @Autowired
    private XfptFinanceFlowMapper xfptFinanceFlowMapper;

    /**
     * 查询资金流水列表
     *
     * @param xfptFinanceFlow 资金流水
     * @return 资金流水
     */
    @Override
    public List<XfptFinanceFlow> selectXfptFinanceFlowList(XfptFinanceFlow xfptFinanceFlow)
    {
        return xfptFinanceFlowMapper.selectXfptFinanceFlowList(xfptFinanceFlow);
    }


}
