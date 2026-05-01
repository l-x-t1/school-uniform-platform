package com.xfgl.supplier.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.supplier.domain.XfptSettlementOrderItem;

/**
 * 结算明细Service接口
 *
 * @author yinet
 * @date 2026-05-01
 */
public interface IXfptSettlementOrderItemService extends IService<XfptSettlementOrderItem> {

    /**
     * 查询结算明细列表
     *
     * @param xfptSettlementOrderItem 结算明细
     * @return 结算明细集合
     */
    public List<XfptSettlementOrderItem> selectXfptSettlementOrderItemList(XfptSettlementOrderItem xfptSettlementOrderItem);

}
