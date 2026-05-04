package com.xfgl.supplier.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.supplier.domain.XfptDeliveryOrder;

/**
 * 发货单主Mapper接口
 *
 * @author yinet
 * @date 2026-05-01
 */
public interface XfptDeliveryOrderMapper extends BaseMapper<XfptDeliveryOrder> {
    /**
     * 查询发货单主列表
     *
     * @param xfptDeliveryOrder 发货单主
     * @return 发货单主集合
     */
    public List<XfptDeliveryOrder> selectXfptDeliveryOrderList(XfptDeliveryOrder xfptDeliveryOrder);
}
