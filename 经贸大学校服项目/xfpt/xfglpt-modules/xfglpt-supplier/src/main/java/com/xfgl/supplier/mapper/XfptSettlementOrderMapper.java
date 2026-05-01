package com.xfgl.supplier.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.supplier.domain.XfptSettlementOrder;

/**
 * 结算单Mapper接口
 *
 * @author yinet
 * @date 2026-05-01
 */
public interface XfptSettlementOrderMapper extends BaseMapper<XfptSettlementOrder> {
    /**
     * 查询结算单列表
     *
     * @param xfptSettlementOrder 结算单
     * @return 结算单集合
     */
    public List<XfptSettlementOrder> selectXfptSettlementOrderList(XfptSettlementOrder xfptSettlementOrder);
}
