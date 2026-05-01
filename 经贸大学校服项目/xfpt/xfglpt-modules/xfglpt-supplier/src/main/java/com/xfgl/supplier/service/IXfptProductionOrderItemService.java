package com.xfgl.supplier.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.supplier.domain.XfptProductionOrderItem;

/**
 * 生产单明细Service接口
 *
 * @author yinet
 * @date 2026-05-01
 */
public interface IXfptProductionOrderItemService extends IService<XfptProductionOrderItem> {

    /**
     * 查询生产单明细列表
     *
     * @param xfptProductionOrderItem 生产单明细
     * @return 生产单明细集合
     */
    public List<XfptProductionOrderItem> selectXfptProductionOrderItemList(XfptProductionOrderItem xfptProductionOrderItem);

}
