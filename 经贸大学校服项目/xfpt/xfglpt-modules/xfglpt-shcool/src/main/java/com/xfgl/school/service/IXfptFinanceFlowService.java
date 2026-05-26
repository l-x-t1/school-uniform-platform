package com.xfgl.school.service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.XfptFinanceFlow;

/**
 * 资金流水Service接口
 *
 * @author yinet
 * @date 2026-05-04
 */
public interface IXfptFinanceFlowService extends IService<XfptFinanceFlow> {

    /**
     * 查询资金流水列表
     *
     * @param xfptFinanceFlow 资金流水
     * @return 资金流水集合
     */
    public List<XfptFinanceFlow> selectXfptFinanceFlowList(XfptFinanceFlow xfptFinanceFlow);

}
