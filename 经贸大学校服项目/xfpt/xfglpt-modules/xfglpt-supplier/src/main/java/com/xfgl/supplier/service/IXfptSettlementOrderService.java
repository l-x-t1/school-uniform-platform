package com.xfgl.supplier.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.supplier.domain.XfptSettlementOrder;

/**
 * 结算单Service接口
 *
 * @author yinet
 * @date 2026-05-01
 */
public interface IXfptSettlementOrderService extends IService<XfptSettlementOrder> {

    /**
     * 查询结算单列表
     *
     * @param xfptSettlementOrder 结算单
     * @return 结算单集合
     */
    public List<XfptSettlementOrder> selectXfptSettlementOrderList(XfptSettlementOrder xfptSettlementOrder);

}
