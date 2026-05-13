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

    /**
     * 查询每日入账金额列表
     *
     * @param xfptFinanceFlow 资金流水筛选条件
     * @return 每日入账金额集合
     */
    @Override
    public List<Map<String, Object>> selectDailyIncomeList(XfptFinanceFlow xfptFinanceFlow)
    {
        return xfptFinanceFlowMapper.selectDailyIncomeList(xfptFinanceFlow);
    }

    /**
     * 查询每日入账金额详情
     *
     * @param xfptFinanceFlow 资金流水筛选条件
     * @return 资金流水集合
     */
    @Override
    public List<XfptFinanceFlow> selectDailyIncomeDetail(XfptFinanceFlow xfptFinanceFlow)
    {
        return xfptFinanceFlowMapper.selectDailyIncomeDetail(xfptFinanceFlow);
    }


}
