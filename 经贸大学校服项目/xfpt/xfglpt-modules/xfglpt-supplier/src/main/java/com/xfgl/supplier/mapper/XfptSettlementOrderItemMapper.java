package com.xfgl.supplier.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.supplier.domain.XfptSettlementOrderItem;

/**
 * 结算明细Mapper接口
 *
 * @author yinet
 * @date 2026-05-01
 */
public interface XfptSettlementOrderItemMapper extends BaseMapper<XfptSettlementOrderItem> {
    /**
     * 查询结算明细列表
     *
     * @param xfptSettlementOrderItem 结算明细
     * @return 结算明细集合
     */
    public List<XfptSettlementOrderItem> selectXfptSettlementOrderItemList(XfptSettlementOrderItem xfptSettlementOrderItem);
}
