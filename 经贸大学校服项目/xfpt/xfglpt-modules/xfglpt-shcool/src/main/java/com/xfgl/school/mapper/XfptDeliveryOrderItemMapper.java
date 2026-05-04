package com.xfgl.school.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.school.domain.XfptDeliveryOrderItem;

/**
 * 发货单明细Mapper接口
 *
 * @author yinet
 * @date 2026-05-04
 */
public interface XfptDeliveryOrderItemMapper extends BaseMapper<XfptDeliveryOrderItem> {
    /**
     * 查询发货单明细列表
     *
     * @param xfptDeliveryOrderItem 发货单明细
     * @return 发货单明细集合
     */
    public List<XfptDeliveryOrderItem> selectXfptDeliveryOrderItemList(XfptDeliveryOrderItem xfptDeliveryOrderItem);
}
