package com.xfgl.supplier.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.supplier.domain.XfptFinanceFlow;

/**
 * 财务流水Service接口
 *
 * @author yinet
 * @date 2026-05-01
 */
public interface IXfptFinanceFlowService extends IService<XfptFinanceFlow> {

    /**
     * 查询财务流水列表
     *
     * @param xfptFinanceFlow 财务流水
     * @return 财务流水集合
     */
    public List<XfptFinanceFlow> selectXfptFinanceFlowList(XfptFinanceFlow xfptFinanceFlow);

}
