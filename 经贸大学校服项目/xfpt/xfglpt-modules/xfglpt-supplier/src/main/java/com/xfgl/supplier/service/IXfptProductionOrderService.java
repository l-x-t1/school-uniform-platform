package com.xfgl.supplier.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.supplier.domain.XfptProductionOrder;

/**
 * 生产单Service接口
 *
 * @author yinet
 * @date 2026-05-01
 */
public interface IXfptProductionOrderService extends IService<XfptProductionOrder> {

    /**
     * 查询生产单列表
     *
     * @param xfptProductionOrder 生产单
     * @return 生产单集合
     */
    public List<XfptProductionOrder> selectXfptProductionOrderList(XfptProductionOrder xfptProductionOrder);

}
