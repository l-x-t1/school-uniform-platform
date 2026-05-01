package com.xfgl.supplier.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.supplier.domain.XfptDeliveryOrderItem;

/**
 * 发货单明细Service接口
 *
 * @author yinet
 * @date 2026-05-01
 */
public interface IXfptDeliveryOrderItemService extends IService<XfptDeliveryOrderItem> {

    /**
     * 查询发货单明细列表
     *
     * @param xfptDeliveryOrderItem 发货单明细
     * @return 发货单明细集合
     */
    public List<XfptDeliveryOrderItem> selectXfptDeliveryOrderItemList(XfptDeliveryOrderItem xfptDeliveryOrderItem);

}
