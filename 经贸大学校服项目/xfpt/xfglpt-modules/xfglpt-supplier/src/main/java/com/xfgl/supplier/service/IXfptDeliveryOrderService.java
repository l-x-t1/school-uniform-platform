package com.xfgl.supplier.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.supplier.domain.XfptDeliveryOrder;

/**
 * 发货单Service接口
 *
 * @author yinet
 * @date 2026-05-01
 */
public interface IXfptDeliveryOrderService extends IService<XfptDeliveryOrder> {

    /**
     * 查询发货单列表
     *
     * @param xfptDeliveryOrder 发货单
     * @return 发货单集合
     */
    public List<XfptDeliveryOrder> selectXfptDeliveryOrderList(XfptDeliveryOrder xfptDeliveryOrder);

}
